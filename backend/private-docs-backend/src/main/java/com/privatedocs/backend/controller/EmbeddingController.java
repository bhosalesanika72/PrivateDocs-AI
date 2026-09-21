package com.privatedocs.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {

    @GetMapping("/test")
    public Map<String, Object> testEmbedding(
            @RequestParam(value = "text", defaultValue = "Hello World") String text) {

        return Map.of(
                "text", text,
                "status", "Embedding service endpoint is available",
                "message", "Spring AI automatic embedding is disabled. GeminiService is used for AI answers."
        );
    }
}