package com.kivanval.thread;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalyzerText implements Callable<Map<String, Long>> {

    List<Path> srcPaths;

    public AnalyzerText(Path directory) throws IOException {
        try (Stream<Path> paths = Files.list(directory)) {
            this.srcPaths = paths.collect(Collectors.toList());
        }
    }

    @Override
    public Map<String, Long> call() throws Exception {
        List<String> words = srcPaths
                .parallelStream()
                .flatMap(AnalyzerText::silentFilesLines)
                .flatMap(l -> Arrays.stream(l.trim().split("\\s+")))
                .collect(Collectors.toList());
        HashMap<String, Long> map = new HashMap<>();
        words.forEach(w -> map.merge(w, 1L, Long::sum));
        return map;
    }

    private static Stream<String> silentFilesLines(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
