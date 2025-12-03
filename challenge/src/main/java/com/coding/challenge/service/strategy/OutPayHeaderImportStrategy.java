package com.coding.challenge.service.strategy;

import com.coding.challenge.model.entity.OutPayHeader;
import com.coding.challenge.repository.OutPayHeaderRepository;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutPayHeaderImportStrategy implements ImportStrategy {
    private final OutPayHeaderRepository repository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public boolean canHandle(String fileName) {
        return fileName != null && fileName.toUpperCase().startsWith("OUTPH");
    }

    @Override
    public void execute(InputStream inputStream, String fileName) throws IOException {
        log.info("Processing OUTPH file: {}", fileName);
        List<OutPayHeader> outPayHeaders = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder()
                             .withSeparator(';')
                             .withIgnoreQuotations(true)
                             .build())
                     .build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line.length < 2) continue;

                OutPayHeader outPayHeader = setOutPayHeader(line);

                outPayHeaders.add(outPayHeader);
            }
            repository.saveAll(outPayHeaders);
            log.info("Saved {} OUTPH records", outPayHeaders.size());
        } catch (Exception e) {
            log.error("Error parsing OUTPH", e);
            throw new IOException("Error processing file", e);
        }
    }

    private OutPayHeader setOutPayHeader(String[] line) {
        OutPayHeader outPayHeader = new OutPayHeader();

        outPayHeader.setClntnum(getValueSafe(line, 0));
        outPayHeader.setChdrnum(getValueSafe(line, 1));
        outPayHeader.setLetterType(getValueSafe(line, 2));
        outPayHeader.setPrintDate(parseDate(getValueSafe(line, 3)));
        outPayHeader.setDataID(getValueSafe(line, 4));
        outPayHeader.setClntName(getValueSafe(line, 5));
        outPayHeader.setClntAddress(getValueSafe(line, 6));
        outPayHeader.setRegDate(parseDate(getValueSafe(line, 7)));
        outPayHeader.setBenPercent(parseDecimal(getValueSafe(line, 8)));
        outPayHeader.setRole1(getValueSafe(line, 9));
        outPayHeader.setRole2(getValueSafe(line, 10));
        outPayHeader.setCownNum(getValueSafe(line, 11));
        outPayHeader.setCownName(getValueSafe(line, 12));
        outPayHeader.setNotice01(getValueSafe(line, 13));
        outPayHeader.setNotice02(getValueSafe(line, 14));
        outPayHeader.setNotice03(getValueSafe(line, 15));
        outPayHeader.setNotice04(getValueSafe(line, 16));
        outPayHeader.setNotice05(getValueSafe(line, 17));
        outPayHeader.setNotice06(getValueSafe(line, 18));
        outPayHeader.setClaim_ID(getValueSafe(line, 19));
        outPayHeader.setTP2ProcessDate(parseDate(getValueSafe(line, 20)));
        return outPayHeader;
    }

    private String getValueSafe(String[] line, int index) {
        if (index < line.length && line[index] != null) {
            return line[index].trim();
        }
        return null;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("Invalid date format: {}", dateStr);
            return null;
        }
    }

    private BigDecimal parseDecimal(String numStr) {
        if (numStr == null || numStr.isBlank()) return null;
        try {
            return new BigDecimal(numStr.replace(",", "."));
        } catch (Exception e) {
            log.warn("Invalid number format: {}", numStr);
            return null;
        }
    }
}
