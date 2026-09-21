package com.privatedocs.backend.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextChunkerTest {

    @Test
    void returnsEmptyListForNullAndBlankText() {
        assertTrue(TextChunker.splitText(null, 4).isEmpty());
        assertTrue(TextChunker.splitText("   ", 4).isEmpty());
    }

    @Test
    void splitsTextIntoTrimmedChunks() {
        assertEquals(List.of("abcd", "efgh", "ij"), TextChunker.splitText("abcdefghij", 4));
        assertEquals(List.of("ab", "cd"), TextChunker.splitText("  ab  cd  ", 4));
    }
}