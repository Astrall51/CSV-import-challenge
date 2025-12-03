package com.coding.challenge.service;

import com.coding.challenge.model.entity.ImportLog;
import com.coding.challenge.model.entity.ImportStatusEnum;
import com.coding.challenge.repository.ImportLogRepository;
import com.coding.challenge.service.strategy.ImportStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {
    private final List<ImportStrategy> strategies;

    private final ImportLogRepository importLogRepository;

    public void processImport(MultipartFile file) {
        try {
            processImportStream(file.getInputStream(), file.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException("Error reading multipart file", e);
        }
    }

    public void processImportStream(InputStream inputStream, String fileName) {
        log.info("Import process started for file: {}", fileName);

        if (importLogRepository.existsByFileName(fileName)) {
            log.warn("File '{}' has already been processed. Skipping", fileName);
            return;
        }

        ImportStrategy selectedStrategy = strategies.stream()
                .filter(strategy -> strategy.canHandle(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for file: " + fileName));

        try {
            selectedStrategy.execute(inputStream, fileName);

            importLogRepository.save(new ImportLog(fileName, ImportStatusEnum.SUCCESS));
            log.info("File processed successfully: {}", fileName);
        } catch (IOException e) {
            log.error("Error processing file: {}", fileName, e);
            importLogRepository.save(new ImportLog(fileName, ImportStatusEnum.FAILED));

            throw new RuntimeException("Import failed for " + fileName, e);
        }
    }
}
