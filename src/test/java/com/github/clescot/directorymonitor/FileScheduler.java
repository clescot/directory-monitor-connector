package com.github.clescot.directorymonitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileScheduler {

    public static void scheduleFileCreation(Path directoryPath) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("creating temp file");
                final File tempFile = File.createTempFile("test_", ".txt", directoryPath.toFile());
                System.out.println("temp file created "+tempFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 1, 2, TimeUnit.SECONDS);
    }

    public static void scheduleFileDeletion(Path fileToDelete) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> fileToDelete.toFile().delete(), 1, 2, TimeUnit.SECONDS);
    }

    public static void scheduleFileModification(Path fileToDelete) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> fileToDelete.toFile().setLastModified(System.currentTimeMillis()), 1, 2, TimeUnit.SECONDS);
    }
}
