package com.coding.challenge.service;

import com.coding.challenge.service.strategy.ImportStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {
    private final List<ImportStrategy> strategies;

    public void processImport(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.info("Import process started for file: {}", fileName);

        ImportStrategy selectedStrategy = strategies.stream()
                .filter(strategy -> strategy.canHandle(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for file: {}" + fileName));

        try {
            selectedStrategy.execute(file);
            log.info("File processed successfully: {}", fileName);
        } catch (IOException e) {
            log.error("Error processing file: {}", fileName, e);
            throw new RuntimeException("Import failed", e);
        }
    }
}
