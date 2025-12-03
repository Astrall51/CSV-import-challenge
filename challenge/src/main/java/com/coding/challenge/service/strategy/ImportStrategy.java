package com.coding.challenge.service.strategy;

import java.io.IOException;
import java.io.InputStream;

public interface ImportStrategy {
    boolean canHandle(String fileName);

    void execute(InputStream inputStream, String fileName) throws IOException;
}
