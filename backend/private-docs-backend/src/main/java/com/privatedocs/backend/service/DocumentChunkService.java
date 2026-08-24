package com.privatedocs.backend.service;

import com.privatedocs.backend.entity.DocumentChunk;
import java.util.List;

public interface DocumentChunkService {

    List<DocumentChunk> createChunks(
            Long documentId,
            String text
    );

    List<DocumentChunk> getChunksByDocumentId(
            Long documentId
    );
}