package com.coding.challenge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduledImportService {
    private final ImportService importService;

    @Value("${app.import.input-dir}")
    private String inputDir;

    @Value("${app.import.archive-dir}")
    private String archiveDir;

    @Scheduled(cron = "${cron.expression.daily}")
    public void runDailyImport() {
        log.info("Running scheduled import task...");

        File folder = new File(inputDir);
        if (!folder.exists()) {
            folder.mkdirs();
            new File(archiveDir).mkdirs();
            return;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            log.info("No files found in input directory.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                processFile(file);
            }
        }
    }

    private void processFile(File file) {
        log.info("Found file {}", file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {
            importService.processImportStream(fis, file.getName());

            Files.move(file.toPath(), Path.of(archiveDir, file.getName()), StandardCopyOption.REPLACE_EXISTING);
            log.info("File archived: {}", file.getName());
        } catch (Exception e) {
            log.error("Failed to process scheduled file: {}", file.getName(), e);
        }
    }
}
