package com.kivanval;

import com.kivanval.thread.AnalyzerTextPool;
import com.kivanval.thread.ResourceStrategy;

import java.nio.file.FileSystems;
import java.nio.file.Path;


public class MultithreadingApplication {
    public static void main(String[] args) throws InterruptedException {
        Path dir = FileSystems.getDefault().getPath("src/main/resources/dir");
        Path file = FileSystems.getDefault().getPath("src/main/resources/srcTwo");
        AnalyzerTextPool pool = AnalyzerTextPool.builder()
                .addThread(dir, ResourceStrategy.DIRECTORY)
                .addThread(file, ResourceStrategy.FILE)
                .build();
        System.out.println(pool.execute());
        pool.shutdown();
    }
}