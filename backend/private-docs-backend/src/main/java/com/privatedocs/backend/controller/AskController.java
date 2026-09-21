package com.privatedocs.backend.controller;

import com.privatedocs.backend.entity.DocumentChunk;
import com.privatedocs.backend.repository.DocumentChunkRepository;
import com.privatedocs.backend.service.GeminiService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AskController {

    private final DocumentChunkRepository documentChunkRepository;
    private final GeminiService geminiService;

    public AskController(
            DocumentChunkRepository documentChunkRepository,
            GeminiService geminiService
    ) {
        this.documentChunkRepository = documentChunkRepository;
        this.geminiService = geminiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> ask(
            @RequestBody Map<String, String> request
    ) {

        long start = System.currentTimeMillis();

        String question = request.get("question");

        // -----------------------------------------
        // VALIDATE QUESTION
        // -----------------------------------------

        if (question == null || question.trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(createResponse(
                            "",
                            "Please enter a question.",
                            0
                    ));
        }

        question = question.trim();

        System.out.println();
        System.out.println("=================================");
        System.out.println("AI QUESTION RECEIVED");
        System.out.println(question);
        System.out.println("=================================");

        // -----------------------------------------
        // LOAD CHUNKS
        // -----------------------------------------

        long dbStart = System.currentTimeMillis();

        List<DocumentChunk> allChunks =
                documentChunkRepository.findAll();

        long dbEnd = System.currentTimeMillis();

        System.out.println(
                "Loaded "
                        + allChunks.size()
                        + " chunks in "
                        + (dbEnd - dbStart)
                        + " ms"
        );

        // -----------------------------------------
        // NO DOCUMENTS
        // -----------------------------------------

        if (allChunks.isEmpty()) {

            return ResponseEntity.ok(
                    createResponse(
                            question,
                            "No documents have been uploaded yet.",
                            0
                    )
            );
        }

        // -----------------------------------------
        // FIND RELEVANT CHUNKS
        // -----------------------------------------

        long searchStart = System.currentTimeMillis();

        List<DocumentChunk> relevantChunks =
                findRelevantChunks(
                        question,
                        allChunks
                );

        long searchEnd = System.currentTimeMillis();

        System.out.println(
                "Relevant chunks: "
                        + relevantChunks.size()
                        + " found in "
                        + (searchEnd - searchStart)
                        + " ms"
        );

        // -----------------------------------------
        // NO MATCH
        // -----------------------------------------

        if (relevantChunks.isEmpty()) {

            return ResponseEntity.ok(
                    createResponse(
                            question,
                            "I could not find relevant information in the uploaded documents.",
                            0
                    )
            );
        }

        // -----------------------------------------
        // BUILD CONTEXT
        // -----------------------------------------

        StringBuilder contextBuilder =
                new StringBuilder();

        /*
         * Never send too many chunks to Gemini.
         */
        int maxChunks = Math.min(
                relevantChunks.size(),
                5
        );

        for (int i = 0; i < maxChunks; i++) {

            DocumentChunk chunk =
                    relevantChunks.get(i);

            if (chunk == null) {
                continue;
            }

            String text = chunk.getChunkText();

            if (text == null || text.isBlank()) {
                continue;
            }

            contextBuilder
                    .append(text.trim())
                    .append("\n\n");
        }

        String context =
                contextBuilder
                        .toString()
                        .trim();

        /*
         * Additional safety limit.
         */
        if (context.length() > 12000) {
            context =
                    context.substring(0, 12000);
        }

        System.out.println(
                "Context length: "
                        + context.length()
                        + " characters"
        );

        // -----------------------------------------
        // GEMINI
        // -----------------------------------------

        String answer;

        try {

            long aiStart =
                    System.currentTimeMillis();

            answer =
                    geminiService.generateAnswer(
                            question,
                            context
                    );

            long aiEnd =
                    System.currentTimeMillis();

            System.out.println(
                    "AI generation took "
                            + (aiEnd - aiStart)
                            + " ms"
            );

        } catch (Exception e) {

            e.printStackTrace();

            answer =
                    "Unable to generate an AI answer right now.";
        }

        // -----------------------------------------
        // FINAL RESPONSE
        // -----------------------------------------

        long end =
                System.currentTimeMillis();

        System.out.println(
                "Total /api/ask time: "
                        + (end - start)
                        + " ms"
        );

        System.out.println(
                "================================="
        );

        return ResponseEntity.ok(
                createResponse(
                        question,
                        answer,
                        relevantChunks.size()
                )
        );
    }

    // =========================================================
    // FIND RELEVANT CHUNKS
    // =========================================================

    private List<DocumentChunk> findRelevantChunks(
            String question,
            List<DocumentChunk> chunks
    ) {

        String normalizedQuestion =
                normalize(question);

        Set<String> questionWords =
                tokenize(normalizedQuestion);

        if (questionWords.isEmpty()) {
            return Collections.emptyList();
        }

        List<ScoredChunk> scored =
                new ArrayList<>();

        for (DocumentChunk chunk : chunks) {

            if (chunk == null) {
                continue;
            }

            String text =
                    chunk.getChunkText();

            if (text == null || text.isBlank()) {
                continue;
            }

            String normalizedText =
                    normalize(text);

            Set<String> textWords =
                    tokenize(normalizedText);

            if (textWords.isEmpty()) {
                continue;
            }

            int score = 0;

            for (String word : questionWords) {

                if (textWords.contains(word)) {
                    score++;
                }
            }

            /*
             * Phrase match gives extra weight.
             */
            if (normalizedText.contains(
                    normalizedQuestion
            )) {
                score += 5;
            }

            if (score > 0) {

                scored.add(
                        new ScoredChunk(
                                chunk,
                                score
                        )
                );
            }
        }

        return scored.stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        ScoredChunk::score
                                )
                                .reversed()
                )
                .limit(5)
                .map(ScoredChunk::chunk)
                .collect(Collectors.toList());
    }

    // =========================================================
    // NORMALIZE TEXT
    // =========================================================

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // =========================================================
    // TOKENIZE
    // =========================================================

    private Set<String> tokenize(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }

        Set<String> stopWords =
                Set.of(
                        "the",
                        "is",
                        "are",
                        "was",
                        "were",
                        "a",
                        "an",
                        "and",
                        "or",
                        "of",
                        "to",
                        "in",
                        "on",
                        "for",
                        "with",
                        "what",
                        "which",
                        "who",
                        "how",
                        "why",
                        "when",
                        "where",
                        "do",
                        "does",
                        "did",
                        "i",
                        "me",
                        "my",
                        "you",
                        "your"
                );

        return Arrays.stream(
                        text.split("\\s+")
                )
                .map(String::trim)
                .filter(word ->
                        word.length() > 2
                )
                .filter(word ->
                        !stopWords.contains(word)
                )
                .collect(Collectors.toSet());
    }

    // =========================================================
    // RESPONSE
    // =========================================================

    private Map<String, Object> createResponse(
            String question,
            String answer,
            int matchedChunks
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "question",
                question
        );

        response.put(
                "answer",
                answer
        );

        response.put(
                "matchedChunks",
                matchedChunks
        );

        return response;
    }

    // =========================================================
    // INTERNAL SCORE CLASS
    // =========================================================

    private static class ScoredChunk {

        private final DocumentChunk chunk;
        private final int score;

        public ScoredChunk(
                DocumentChunk chunk,
                int score
        ) {
            this.chunk = chunk;
            this.score = score;
        }

        public DocumentChunk chunk() {
            return chunk;
        }

        public int score() {
            return score;
        }
    }
}