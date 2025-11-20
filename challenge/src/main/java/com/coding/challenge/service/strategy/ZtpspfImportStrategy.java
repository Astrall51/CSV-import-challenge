package com.coding.challenge.service.strategy;

import com.coding.challenge.model.entity.SurValues;
import com.coding.challenge.repository.SurValuesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ZtpspfImportStrategy implements ImportStrategy {
    private final SurValuesRepository repository;


    @Override
    public boolean canHandle(String fileName) {
        return fileName != null && fileName.toUpperCase().startsWith("ZTPSPF");
    }

    @Override
    public void execute(MultipartFile file) throws IOException {
        log.info("Processing ZTPSPF (Fixed Width) file: {}", file.getOriginalFilename());
        List<SurValues> surValues = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                if (line.length() < 54) {
                    log.warn("Skipping short line (len={}): {}", line.length(), line);
                    continue;
                }

                SurValues surValue = new SurValues();

                surValue.setCompany(line.substring(0, 1));
                surValue.setChdrnum(line.substring(1, 9).trim());
                String amountStr = line.substring(9, 24).trim();
                try {
                    surValue.setSurrenderValue(new BigDecimal(amountStr));
                } catch (NumberFormatException e) {
                    log.warn("Invalid number format in line: {}", line);
                    continue;
                }
                if (line.length() >= 54) {
                    surValue.setValidDate(line.substring(44, 54).trim());
                }

                surValues.add(surValue);
            }
            repository.saveAll(surValues);
            log.info("Saved {} SurValues records.", surValues.size());

        } catch (Exception e) {
            log.error("Error parsing ZTPSPF", e);
            throw new IOException("Error processing fixed width file", e);
        }
    }
}
