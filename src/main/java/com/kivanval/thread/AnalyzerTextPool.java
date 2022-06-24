package com.kivanval.thread;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AnalyzerTextPool {
    private final List<Callable<Map<String, Long>>> callables = new ArrayList<>();

    private ExecutorService executorService;

    public static AnalyzerTextPoolBuilder builder() {
        return new AnalyzerTextPoolBuilder();
    }

    public static class AnalyzerTextPoolBuilder {
        AnalyzerTextPool pool = new AnalyzerTextPool();

        public AnalyzerTextPoolBuilder addThread(Path directory) throws IOException {
            if (Files.isDirectory(directory)) {
                pool.callables.add(new AnalyzerText(directory));
            } else {
                throw new RuntimeException();
            }
            return this;
        }

        public AnalyzerTextPool build() {
            pool.executorService = Executors.newFixedThreadPool(pool.callables.size());
            return pool;
        }
    }

    public SummarizeInformation execute() throws InterruptedException {
        Map<String, Long> map = new ConcurrentHashMap<>();

        executorService.invokeAll(callables)
                .parallelStream()
                .map(AnalyzerTextPool::silentFutureGet)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .forEach(e -> map.merge(e.getKey(), e.getValue(), Long::sum));
        executorService.shutdown();

        LongSummaryStatistics statistics = map.values()
                .parallelStream()
                .mapToLong(Long::longValue)
                .summaryStatistics();
        SummarizeInformation textStatistics = new SummarizeInformation();

        long min = statistics.getMin();
        List<String> minWords = map.entrySet().
                parallelStream()
                .filter(e -> e.getValue().equals(min))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        textStatistics.setMin(min);
        textStatistics.setMinWords(minWords);

        long max = statistics.getMax();
        List<String> maxWords = map.entrySet()
                .parallelStream()
                .filter(e -> e.getValue().equals(max))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        textStatistics.setMax(max);
        textStatistics.setMaxWords(maxWords);

        long count = statistics.getSum();
        textStatistics.setCount(count);

        return textStatistics;

    }

    private static <T> T silentFutureGet(Future<T> f) {
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
