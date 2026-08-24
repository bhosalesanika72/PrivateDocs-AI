package com.privatedocs.backend.util;

import java.util.ArrayList;
import java.util.List;

public class TextChunker {

    public static List<String> splitText(
            String text,
            int chunkSize) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return chunks;
        }

        int start = 0;

        while (start < text.length()) {

            int end =
                    Math.min(
                            start + chunkSize,
                            text.length()
                    );

            String chunk =
                    text.substring(start, end).trim();

            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            start = end;
        }

        return chunks;
    }
}