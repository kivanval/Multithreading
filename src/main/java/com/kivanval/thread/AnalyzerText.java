package com.kivanval.thread;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingByConcurrent;

public class AnalyzerText implements Callable<Map<String, Long>> {

    Collection<Path> srcPaths;

    public AnalyzerText(Collection<Path> paths) {
        this.srcPaths = paths;
    }

    @Override
    public Map<String, Long> call() {
        return srcPaths.parallelStream()
                .flatMap(AnalyzerText::silentFilesLines)
                .map(l -> l.replaceAll("[\\s\\p{P}]+", " "))
                .map(String::trim)
                .map(l -> l.split(" "))
                .flatMap(Arrays::stream)
                .collect(groupingByConcurrent(Function.identity(), counting()));
    }

    private static Stream<String> silentFilesLines(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
