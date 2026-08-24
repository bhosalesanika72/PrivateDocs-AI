package com.privatedocs.backend.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class DocumentTextExtractor {

    private static final Tika tika = new Tika();

    public static String extractText(MultipartFile file) throws IOException {

        try (InputStream inputStream = file.getInputStream()) {

            try {
                return tika.parseToString(inputStream);
            } catch (Exception e) {
                throw new IOException("Unable to extract text from document", e);
            }
        }
    }
}