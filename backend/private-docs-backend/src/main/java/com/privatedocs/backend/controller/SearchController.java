package com.privatedocs.backend.controller;

import com.privatedocs.backend.entity.DocumentChunk;
import com.privatedocs.backend.repository.DocumentChunkRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    private final DocumentChunkRepository documentChunkRepository;

    public SearchController(
            DocumentChunkRepository documentChunkRepository) {

        this.documentChunkRepository = documentChunkRepository;
    }

    @PostMapping
    public ResponseEntity<?> search(
            @RequestBody SearchRequest request) {

        try {

            String query = request.getQuery();

            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Search query cannot be empty");
            }

            List<DocumentChunk> allChunks =
                    documentChunkRepository.findAll();

            List<DocumentChunk> results =
                    new ArrayList<>();

            String searchText =
                    query.toLowerCase();

            for (DocumentChunk chunk : allChunks) {

                if (chunk.getChunkText() != null &&
                        chunk.getChunkText()
                                .toLowerCase()
                                .contains(searchText)) {

                    results.add(chunk);
                }
            }

            return ResponseEntity.ok(results);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Search error: " + e.getMessage());
        }
    }

    public static class SearchRequest {

        private String query;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }
}