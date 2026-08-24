package com.privatedocs.backend.service;

import com.privatedocs.backend.entity.DocumentChunk;
import com.privatedocs.backend.repository.DocumentChunkRepository;
import com.privatedocs.backend.util.TextChunker;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChunkServiceImpl implements DocumentChunkService {

    private final DocumentChunkRepository chunkRepository;

    public DocumentChunkServiceImpl(
            DocumentChunkRepository chunkRepository) {

        this.chunkRepository = chunkRepository;
    }

    @Override
    public List<DocumentChunk> createChunks(
            Long documentId,
            String text) {

        List<DocumentChunk> savedChunks =
                new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {

            System.out.println(
                    "WARNING: No text extracted from document ID: "
                            + documentId
            );

            return savedChunks;
        }

        List<String> chunks =
                TextChunker.splitText(text, 1000);

        System.out.println(
                "Document ID " + documentId +
                " generated " + chunks.size() +
                " chunks."
        );

        for (int i = 0; i < chunks.size(); i++) {

            String chunkText = chunks.get(i);

            if (chunkText == null ||
                    chunkText.trim().isEmpty()) {
                continue;
            }

            DocumentChunk chunk =
                    new DocumentChunk();

            chunk.setDocumentId(documentId);
            chunk.setChunkIndex(i);
            chunk.setChunkText(chunkText);

            DocumentChunk savedChunk =
                    chunkRepository.save(chunk);

            savedChunks.add(savedChunk);

            System.out.println(
                    "Saved chunk " + i +
                    " for document " + documentId
            );
        }

        System.out.println(
                "Total saved chunks: "
                        + savedChunks.size()
        );

        return savedChunks;
    }

    @Override
    public List<DocumentChunk> getChunksByDocumentId(
            Long documentId) {

        return chunkRepository.findByDocumentId(documentId);
    }
}