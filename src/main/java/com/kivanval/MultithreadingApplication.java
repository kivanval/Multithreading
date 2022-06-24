package com.kivanval;

import com.kivanval.thread.AnalyzerTextPool;
import com.kivanval.thread.ResourceStrategy;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;


public class MultithreadingApplication {
    public static void main(String[] args) throws InterruptedException {
        Path pathDirOne = FileSystems.getDefault().getPath("src/main/resources/threadOne");
        Path pathDirTwo = FileSystems.getDefault().getPath("src/main/resources/threadTwo");
        AnalyzerTextPool pool = AnalyzerTextPool.builder()
                .addThread(pathDirOne, ResourceStrategy.RECURSIVE)
                .build();
        System.out.println(pool.execute());
        pool.shutdown();
    }
}