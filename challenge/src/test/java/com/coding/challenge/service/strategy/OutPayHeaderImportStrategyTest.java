package com.coding.challenge.service.strategy;

import com.coding.challenge.model.entity.OutPayHeader;
import com.coding.challenge.repository.OutPayHeaderRepository;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OutPayHeaderImportStrategyTest {
    @Mock
    private OutPayHeaderRepository repository;

    @InjectMocks
    private OutPayHeaderImportStrategy strategy;

    @Test
    void canHandle_ShouldReturnTrue_ForCorrectFileName() {
        assertTrue(strategy.canHandle("OUTPH_CUP_20200204.TXT"));
        assertTrue(strategy.canHandle("outph_test.txt"));
    }

    @Test
    void execute_ShouldParseDateAndDecimalCorrectly() throws IOException {
        String csvContent = "20930093;70027344;CUP;20200210;OUTPAY;Kovács Lajos;Budapest;20200204;100.00;OW";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "OUTPH.txt",
                "text/plain",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        strategy.execute(file);

        ArgumentCaptor<List<OutPayHeader>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<OutPayHeader> savedEntities = captor.getValue();
        assertEquals(1, savedEntities.size());

        OutPayHeader entity = savedEntities.get(0);
        assertEquals(LocalDate.of(2020, 2, 10), entity.getPrintDate());
        assertEquals(0, new BigDecimal("100.00").compareTo(entity.getBenPercent()));

        assertEquals("CUP", entity.getLetterType());
        assertEquals("Kovács Lajos", entity.getClntName());
    }
}
