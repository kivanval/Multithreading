package com.kivanval.thread;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ResourceStrategy {
    private ResourceStrategy() {

    }

    public static Function<Path, ? extends Collection<Path>> SINGLE_RESOURCE = Collections::singleton;

    public static Function<Path, ? extends Collection<Path>> DIRECTORY_RESOURCE = (path) -> {
        try (Stream<Path> paths = Files.list(path).filter(Files::isRegularFile)) {
            return paths.collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    public static Function<Path, ? extends Collection<Path>> RECURSIVE_RESOURCE = (path) -> {
        List<Path> paths = new ArrayList<>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                    paths.add(path);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paths;
    };
}