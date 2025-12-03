package com.coding.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledImportServiceTest {
    @Mock
    private ImportService importService;

    @InjectMocks
    private ScheduledImportService scheduledService;

    @TempDir
    Path tempDir;

    private Path inputFolderPath;
    private Path archiveFolderPath;

    @BeforeEach
    void setUp() throws IOException {
        inputFolderPath = tempDir.resolve("input");
        archiveFolderPath = tempDir.resolve("archive");

        Files.createDirectories(inputFolderPath);
        Files.createDirectories(archiveFolderPath);

        ReflectionTestUtils.setField(scheduledService, "inputDir", inputFolderPath.toAbsolutePath().toString());
        ReflectionTestUtils.setField(scheduledService, "archiveDir", archiveFolderPath.toAbsolutePath().toString());
    }

    @Test
    void runDailyImport_ShouldProcessDailyFiles_AndIgnoreWeekly() throws IOException {
        if (!Files.exists(inputFolderPath)) {
            Files.createDirectories(inputFolderPath);
        }

        Files.createFile(inputFolderPath.resolve("CUSTCOMP01.txt"));
        Files.createFile(inputFolderPath.resolve("OUTPH_TEST.txt"));
        Path weeklyFilePath = Files.createFile(inputFolderPath.resolve("ZTPSPF.txt"));

        scheduledService.runDailyImport();

        verify(importService, times(1)).processImportStream(any(), eq("CUSTCOMP01.txt"));
        verify(importService, times(1)).processImportStream(any(), eq("OUTPH_TEST.txt"));

        verify(importService, never()).processImportStream(any(), eq("ZTPSPF.txt"));

        assertFalse(Files.exists(inputFolderPath.resolve("CUSTCOMP01.txt")));
        assertTrue(Files.exists(archiveFolderPath.resolve("CUSTCOMP01.txt")));

        assertTrue(Files.exists(weeklyFilePath));
    }

    @Test
    void runWeeklyImport_ShouldProcessOnlyZtpspf() throws IOException {
        Path dailyFilePath = Files.createFile(inputFolderPath.resolve("CUSTCOMP01.txt"));
        Files.createFile(inputFolderPath.resolve("ZTPSPF.txt"));

        scheduledService.runWeeklyImport();

        verify(importService, times(1)).processImportStream(any(), eq("ZTPSPF.txt"));

        verify(importService, never()).processImportStream(any(), eq("CUSTCOMP01.txt"));

        assertTrue(Files.exists(archiveFolderPath.resolve( "ZTPSPF.txt")));

        assertTrue(Files.exists(dailyFilePath));
    }
}
