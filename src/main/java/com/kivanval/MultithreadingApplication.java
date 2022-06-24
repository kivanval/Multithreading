package com.kivanval;

import com.kivanval.thread.AnalyzerTextPool;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;


public class MultithreadingApplication {
    public static void main(String[] args) throws IOException {
        Path pathDirOne = FileSystems.getDefault().getPath("src/main/resources/threadOne");
        Path pathDirTwo = FileSystems.getDefault().getPath("src/main/resources/threadTwo");
        System.out.println(
                AnalyzerTextPool.builder()
                        .addThread(pathDirOne)
                        .addThread(pathDirTwo)
                        .build()
                        .execute()
        );
    }
}