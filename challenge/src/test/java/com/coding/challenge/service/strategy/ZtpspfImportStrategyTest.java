package com.coding.challenge.service.strategy;

import com.coding.challenge.model.entity.SurValues;
import com.coding.challenge.repository.SurValuesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ZtpspfImportStrategyTest {
    @Mock
    private SurValuesRepository repository;

    @InjectMocks
    private ZtpspfImportStrategy strategy;

    @Test
    void execute_ShouldParseFixedColumnsCorrectly() throws IOException {
        String fileContent =
                """
                130052881     3276866.00K5003MT   WEEKEND1  2020-02-15-08.19.59.017770
                199999999         100.00FillerText          2022-01-01
                1RövidSor   10.00""";

        MockMultipartFile file = new MockMultipartFile(
                "file", "ZTPSPF.TXT", "text/plain",
                fileContent.getBytes(StandardCharsets.UTF_8)
        );

        strategy.execute(file);

        ArgumentCaptor<List<SurValues>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<SurValues> savedEntities = captor.getValue();

        assertEquals(2, savedEntities.size());

        SurValues first = savedEntities.get(0);
        assertEquals("1", first.getCompany());
        assertEquals("30052881", first.getChdrnum());
        assertEquals(0, new BigDecimal("3276866.00").compareTo(first.getSurrenderValue()));
        assertEquals("2020-02-15", first.getValidDate());

        SurValues second = savedEntities.get(1);
        assertEquals("1", second.getCompany());
        assertEquals("99999999", second.getChdrnum());
        assertEquals("2022-01-01", second.getValidDate());
    }
}
