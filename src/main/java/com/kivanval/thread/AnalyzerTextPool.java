package com.kivanval.thread;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class AnalyzerTextPool {
    private List<Callable<Map<String, Long>>> callables;

    private ExecutorService executorService;

    public static AnalyzerTextPoolBuilder builder() {
        return new AnalyzerTextPoolBuilder();
    }

    public static class AnalyzerTextPoolBuilder {
        List<Callable<Map<String, Long>>> callables = new ArrayList<>();

        public AnalyzerTextPoolBuilder addThread(Path path, Function<Path, ? extends Collection<Path>> strategy) {
            callables.add(new AnalyzerText(strategy.apply(path)));
            return this;
        }

        public AnalyzerTextPoolBuilder addThread(Path path) {
            callables.add(new AnalyzerText(ResourceStrategy.FILE.apply(path)));
            return this;
        }

        public AnalyzerTextPool build() {
            AnalyzerTextPool pool = new AnalyzerTextPool();
            pool.executorService = Executors.newFixedThreadPool(callables.size());
            pool.callables = Collections.unmodifiableList(callables);
            return pool;
        }
    }

    public SummarizeInformation execute() throws InterruptedException {
        Map<String, Long> map = executorService.invokeAll(callables)
                .parallelStream()
                .map(AnalyzerTextPool::silentFutureGet)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(groupingByConcurrent(Map.Entry::getKey, summingLong(Map.Entry::getValue)));

        LongSummaryStatistics statistics = map.values()
                .parallelStream()
                .mapToLong(Long::longValue)
                .summaryStatistics();
        SummarizeInformation textStatistics = new SummarizeInformation();

        long min = statistics.getMin();
        List<String> minWords = wordFrequencyIs(map, min);
        textStatistics.setMin(min);
        textStatistics.setMinWords(minWords);

        long max = statistics.getMax();
        List<String> maxWords = wordFrequencyIs(map, max);
        textStatistics.setMax(max);
        textStatistics.setMaxWords(maxWords);

        long count = statistics.getSum();
        textStatistics.setCount(count);

        return textStatistics;

    }

    private List<String> wordFrequencyIs(Map<String, Long> map, Long n) {
        return map.entrySet()
                .parallelStream()
                .filter(e -> e.getValue().equals(n))
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    private static <T> T silentFutureGet(Future<T> f) {
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

}
