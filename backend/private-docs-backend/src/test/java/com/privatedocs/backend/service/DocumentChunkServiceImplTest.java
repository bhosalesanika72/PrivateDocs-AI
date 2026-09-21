package com.privatedocs.backend.service;

import com.privatedocs.backend.entity.DocumentChunk;
import com.privatedocs.backend.repository.DocumentChunkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentChunkServiceImplTest {

    @Mock
    private DocumentChunkRepository chunkRepository;

    @InjectMocks
    private DocumentChunkServiceImpl service;

    @Test
    void returnsEmptyListWithoutSavingForBlankText() {
        assertTrue(service.createChunks(7L, "  ").isEmpty());
        verify(chunkRepository, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createsAndSavesChunksWithDocumentMetadata() {
        when(chunkRepository.save(org.mockito.ArgumentMatchers.any(DocumentChunk.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<DocumentChunk> chunks = service.createChunks(7L, "a".repeat(1001));

        assertEquals(2, chunks.size());
        assertEquals(7L, chunks.get(0).getDocumentId());
        assertEquals(0, chunks.get(0).getChunkIndex());
        assertEquals(1000, chunks.get(0).getChunkText().length());
        assertEquals(1, chunks.get(1).getChunkIndex());
        assertEquals(1, chunks.get(1).getChunkText().length());
    }

    @Test
    void delegatesChunkLookupToRepository() {
        List<DocumentChunk> expected = List.of(new DocumentChunk());
        when(chunkRepository.findByDocumentId(7L)).thenReturn(expected);

        assertEquals(expected, service.getChunksByDocumentId(7L));
        verify(chunkRepository).findByDocumentId(7L);
    }
}