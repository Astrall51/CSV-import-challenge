package com.coding.challenge.service.strategy;

import com.coding.challenge.model.entity.Policy;
import com.coding.challenge.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustCompImportStrategyTest {
    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private CustCompImportStrategy strategy;

    @Test
    void canHandle_ShouldReturnTrue_ForValidFileName() {
        assertTrue(strategy.canHandle("CUSTCOMP01.TXT"));
        assertTrue(strategy.canHandle("custcomp99.txt"));
    }

    @Test
    void  canHandle_ShouldReturnFalse_ForInvalidFileName() {
        assertFalse(strategy.canHandle("OTHERFILE.TXT"));
        assertFalse(strategy.canHandle(null));
    }

    @Test
    void execute_ShouldParseCsvAndSaveToRepository() throws IOException {
        String fileContent = "86000019|76000018|Szegedi István |76000018|Szegedi István |00X|11111|6436 Budapest Rév u. 27. |\n" +
                            "86000029|76000027|Fehér Katalin |76000027|Fehér Katalin |00X|11111|2345 Tatabánya Kossuth tér 6. II.209. |";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "CUSTCOMP01.TXT",
                "text/plain",
                fileContent.getBytes(StandardCharsets.UTF_8)
        );

        strategy.execute(file.getInputStream(), "CUSTCOMP01.TXT");

        ArgumentCaptor<List<Policy>> captor = ArgumentCaptor.forClass(List.class);
        verify(policyRepository, times(1)).saveAll(captor.capture());

        List<Policy> savedPolicies = captor.getValue();

        assertEquals(2, savedPolicies.size());

        Policy first = savedPolicies.get(0);
        assertEquals("86000019", first.getChdrnum());
        assertEquals("Szegedi István", first.getOwnerName());

        Policy second = savedPolicies.get(1);
        assertEquals("Fehér Katalin", second.getOwnerName());
    }
}
