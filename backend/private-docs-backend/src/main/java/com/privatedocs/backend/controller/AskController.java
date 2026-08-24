package com.privatedocs.backend.controller;

import com.privatedocs.backend.entity.DocumentChunk;
import com.privatedocs.backend.repository.DocumentChunkRepository;
import com.privatedocs.backend.service.GeminiService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AskController {

    private final DocumentChunkRepository documentChunkRepository;
    private final GeminiService geminiService;

    public AskController(
            DocumentChunkRepository documentChunkRepository,
            GeminiService geminiService) {

        this.documentChunkRepository = documentChunkRepository;
        this.geminiService = geminiService;
    }

    // =========================================================
    // ASK QUESTION
    // =========================================================

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> ask(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        // -----------------------------------------------------
        // 1. Validate question
        // -----------------------------------------------------

        if (question == null || question.trim().isEmpty()) {

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put("question", "");
            response.put(
                    "answer",
                    "Please enter a question."
            );
            response.put("matchedChunks", 0);

            return ResponseEntity.badRequest()
                    .body(response);
        }

        question = question.trim();

        // -----------------------------------------------------
        // 2. Get all document chunks
        // -----------------------------------------------------

        List<DocumentChunk> allChunks =
                documentChunkRepository.findAll();

        if (allChunks.isEmpty()) {

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put("question", question);
            response.put(
                    "answer",
                    "No documents have been uploaded yet."
            );
            response.put("matchedChunks", 0);

            return ResponseEntity.ok(response);
        }

        // -----------------------------------------------------
        // 3. Find relevant chunks
        // -----------------------------------------------------

        List<DocumentChunk> relevantChunks =
                findRelevantChunks(
                        question,
                        allChunks
                );

        if (relevantChunks.isEmpty()) {

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put("question", question);
            response.put(
                    "answer",
                    "I could not find relevant information in the uploaded documents."
            );
            response.put("matchedChunks", 0);

            return ResponseEntity.ok(response);
        }

        // -----------------------------------------------------
        // 4. Build context for Gemini
        // -----------------------------------------------------

        StringBuilder contextBuilder =
                new StringBuilder();

        for (DocumentChunk chunk : relevantChunks) {

            if (chunk.getChunkText() == null ||
                    chunk.getChunkText().isBlank()) {
                continue;
            }

            contextBuilder
                    .append(chunk.getChunkText())
                    .append("\n\n");
        }

        String context =
                contextBuilder.toString().trim();

        // -----------------------------------------------------
        // 5. Send question + context to Gemini
        // -----------------------------------------------------

        String answer;

        try {

            answer = geminiService.generateAnswer(
                    question,
                    context
            );

            if (answer == null ||
                    answer.trim().isEmpty()) {

                answer =
                        "I could not generate an answer from the uploaded document.";
            }

        } catch (Exception e) {

            e.printStackTrace();

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put("question", question);
            response.put(
                    "answer",
                    "Unable to generate the AI answer. Please check the Gemini configuration."
            );
            response.put(
                    "matchedChunks",
                    relevantChunks.size()
            );

            return ResponseEntity
                    .internalServerError()
                    .body(response);
        }

        // -----------------------------------------------------
        // 6. Return Gemini's answer
        // -----------------------------------------------------

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("question", question);
        response.put("answer", answer.trim());
        response.put(
                "matchedChunks",
                relevantChunks.size()
        );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // FIND RELEVANT CHUNKS
    // =========================================================

    private List<DocumentChunk> findRelevantChunks(
            String question,
            List<DocumentChunk> chunks) {

        String cleanedQuestion =
                question
                        .toLowerCase()
                        .replaceAll(
                                "[^a-zA-Z0-9 ]",
                                ""
                        );

        String[] questionWords =
                cleanedQuestion.split("\\s+");

        // Words that should not affect matching
        Set<String> stopWords = Set.of(
                "what",
                "is",
                "are",
                "the",
                "a",
                "an",
                "does",
                "do",
                "did",
                "how",
                "why",
                "when",
                "where",
                "who",
                "which",
                "can",
                "could",
                "would",
                "should",
                "tell",
                "me",
                "about",
                "this",
                "that",
                "these",
                "those",
                "based",
                "only",
                "on",
                "from",
                "uploaded",
                "document",
                "documents"
        );

        List<ChunkScore> scoredChunks =
                new ArrayList<>();

        // -----------------------------------------------------
        // Score every chunk
        // -----------------------------------------------------

        for (DocumentChunk chunk : chunks) {

            String text =
                    chunk.getChunkText();

            if (text == null ||
                    text.isBlank()) {
                continue;
            }

            String lowerText =
                    text.toLowerCase();

            int score = 0;

            for (String word : questionWords) {

                if (word.length() < 2) {
                    continue;
                }

                if (stopWords.contains(word)) {
                    continue;
                }

                if (lowerText.contains(word)) {
                    score++;
                }
            }

            if (score > 0) {

                scoredChunks.add(
                        new ChunkScore(
                                chunk,
                                score
                        )
                );
            }
        }

        // -----------------------------------------------------
        // Sort highest scoring chunks first
        // -----------------------------------------------------

        scoredChunks.sort(
                Comparator
                        .comparingInt(
                                ChunkScore::getScore
                        )
                        .reversed()
                        .thenComparingInt(
                                cs -> cs.getChunk()
                                        .getChunkIndex()
                        )
        );

        // -----------------------------------------------------
        // Return maximum 3 chunks
        // -----------------------------------------------------

        return scoredChunks
                .stream()
                .limit(3)
                .map(ChunkScore::getChunk)
                .filter(Objects::nonNull)
                .toList();
    }


    // =========================================================
    // CHUNK SCORE CLASS
    // =========================================================

    private static class ChunkScore {

        private final DocumentChunk chunk;
        private final int score;

        public ChunkScore(
                DocumentChunk chunk,
                int score) {

            this.chunk = chunk;
            this.score = score;
        }

        public DocumentChunk getChunk() {
            return chunk;
        }

        public int getScore() {
            return score;
        }
    }
}