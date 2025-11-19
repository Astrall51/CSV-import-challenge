package com.coding.challenge.service.strategy;

import com.coding.challenge.model.entity.Policy;
import com.coding.challenge.repository.PolicyRepository;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustCompImportStrategy implements ImportStrategy{
    private final PolicyRepository policyRepository;

    @Override
    public boolean canHandle(String fileName) {
        return fileName != null && fileName.toUpperCase().startsWith("CUSTCOMP");
    }

    @Override
    public void execute(MultipartFile file) throws IOException {
        log.info("Processing CUSTCOMP file: {}", file.getOriginalFilename());

        List<Policy> policiesToSave = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder()
                             .withSeparator('|')
                             .withIgnoreQuotations(true)
                             .build())
                     .build()) {
                 String[] line;
                 while ((line = csvReader.readNext()) != null) {
                     if (line.length < 2) continue;

                     Policy policy = new Policy();

                     policy.setChdrnum(getValueSafe(line, 0));
                     policy.setCownnum(getValueSafe(line, 1));
                     policy.setOwnerName(getValueSafe(line, 2));
                     policy.setLifcNum(getValueSafe(line, 3));
                     policy.setLifcName(getValueSafe(line, 4));
                     policy.setAracde(getValueSafe(line, 5));
                     policy.setAgntnum(getValueSafe(line, 6));
                     policy.setMailAddress(getValueSafe(line, 7));

                     policiesToSave.add(policy);
                 }

                 policyRepository.saveAll(policiesToSave);
                 log.info("Successfully saved {} policies", policiesToSave.size());
        } catch (CsvValidationException e) {
            log.error("CSV Validation error", e);
            throw new IOException("Invalid CSV formoat", e);
        }
    }

    private String getValueSafe(String[] line, int index) {
        if (index < line.length && line[index] != null) {
            return line[index].trim();
        }
        return null;
    }
}
