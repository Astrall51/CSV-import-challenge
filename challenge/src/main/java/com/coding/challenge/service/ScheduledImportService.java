package com.coding.challenge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
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
        log.info("Starting DAILY import task...");
        processFiles((dir, name) -> !name.startsWith("ZTPSPF"));
    }

    @Scheduled(cron = "${cron.expression.weekly}")
    public void runWeeklyImport() {
        log.info("Starting WEEKLY import task...");
        processFiles((dir, name) -> name.startsWith("ZTPSPF"));
    }

    private void processFiles(FilenameFilter filter) {
        File folder = new File(inputDir);

        if (!folder.exists() || !folder.isDirectory()) {
            log.warn("Input directory does not exist or is not a directory: {}", inputDir);
            return;
        }

        File[] files = folder.listFiles(filter);

        if (files == null || files.length == 0) {
            log.info("No matching files found to process.");
            return;
        }

        log.info("Found {} file(s) to process.", files.length);

        try {
            Files.createDirectories(Path.of(archiveDir));
        } catch (IOException e) {
            log.error("Could not create archive directory: {}. Stopping process", archiveDir, e);
            return;
        }

        for (File file : files) {
            processSingleFile(file);
        }
    }

    private void processSingleFile(File file) {
        log.info("Processing file {}", file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {
            importService.processImportStream(fis, file.getName());
        } catch (Exception e) {
            log.error("Failed to process scheduled file: {}", file.getName(), e);
        }

        try {
            Path targetPath = Path.of(archiveDir, file.getName());
            Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File archived successfully: {}", file.getName());
        } catch (Exception e) {
            log.error("Failed to archive scheduled file: {}", file.getName(), e);
        }
    }
}
