package com.coding.challenge.service.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImportStrategy {
    boolean canHandle(String fileName);

    void execute(MultipartFile file) throws IOException;
}
