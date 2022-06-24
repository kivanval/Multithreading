package com.kivanval.thread;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class AnalyzerTextPool {
    private final List<Callable<Map<String, Long>>> callables = new ArrayList<>();

    private ExecutorService executorService;

    public static AnalyzerTextPoolBuilder builder() {
        return new AnalyzerTextPoolBuilder();
    }

    public static class AnalyzerTextPoolBuilder {
        AnalyzerTextPool pool = new AnalyzerTextPool();

        public AnalyzerTextPoolBuilder addThread(Path path, Function<Path, ? extends Collection<Path>> strategy) throws IOException {
            pool.callables.add(new AnalyzerText(strategy.apply(path)));
            return this;
        }

        public AnalyzerTextPoolBuilder addThread(Path path) throws IOException {
            pool.callables.add(new AnalyzerText(ResourceStrategy.SINGLE_RESOURCE.apply(path)));
            return this;
        }

        public AnalyzerTextPool build() {
            pool.executorService = Executors.newFixedThreadPool(pool.callables.size());
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
