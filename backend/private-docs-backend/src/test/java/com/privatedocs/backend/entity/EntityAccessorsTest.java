package com.privatedocs.backend.entity;

import com.privatedocs.backend.model.Document;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityAccessorsTest {

    @Test
    void userAccessorsAndConstructorPreserveValues() {
        User user = new User("Name", "name@example.com", "secret");
        user.setId(1L);

        assertEquals(1L, user.getId());
        assertEquals("Name", user.getName());
        assertEquals("name@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
    }

    @Test
    void documentAccessorsPreserveValues() {
        Document document = new Document();
        LocalDateTime uploadedAt = LocalDateTime.of(2026, 1, 1, 12, 0);
        document.setFileName("notes.txt");
        document.setFilePath("uploads/notes.txt");
        document.setFileType("text/plain");
        document.setExtractedText("notes");
        document.setUploadedAt(uploadedAt);

        assertEquals("notes.txt", document.getFileName());
        assertEquals("uploads/notes.txt", document.getFilePath());
        assertEquals("text/plain", document.getFileType());
        assertEquals("notes", document.getExtractedText());
        assertEquals(uploadedAt, document.getUploadedAt());
    }

    @Test
    void documentChunkAccessorsPreserveValues() {
        DocumentChunk chunk = new DocumentChunk();
        chunk.setId(2L);
        chunk.setDocumentId(1L);
        chunk.setChunkIndex(3);
        chunk.setChunkText("chunk");

        assertEquals(2L, chunk.getId());
        assertEquals(1L, chunk.getDocumentId());
        assertEquals(3, chunk.getChunkIndex());
        assertEquals("chunk", chunk.getChunkText());
    }
}