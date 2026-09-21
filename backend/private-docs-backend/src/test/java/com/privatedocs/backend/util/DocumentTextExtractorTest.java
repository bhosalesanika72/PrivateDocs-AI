package com.privatedocs.backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentTextExtractorTest {

    @Test
    void extractsPlainTextMultipartFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain", "Private notes".getBytes());

        assertTrue(DocumentTextExtractor.extractText(file).contains("Private notes"));
    }
}