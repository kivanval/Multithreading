package com.kivanval;

import com.kivanval.thread.AnalyzerTextPool;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;


public class MultithreadingApplication {
    public static void main(String[] args) throws IOException, InterruptedException {
        Path pathDirOne = FileSystems.getDefault().getPath("src/main/resources/threadOne");
        Path pathDirTwo = FileSystems.getDefault().getPath("src/main/resources/threadTwo");
        AnalyzerTextPool pool = AnalyzerTextPool.builder()
                .addThread(pathDirOne)
                .addThread(pathDirTwo)
                .build();
        System.out.println(pool.execute());
        pool.shutdown();
    }
}