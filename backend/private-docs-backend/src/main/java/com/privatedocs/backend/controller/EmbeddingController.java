package com.privatedocs.backend.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/test")
    public Map<String, Object> generateEmbedding(
            @RequestParam(value = "text", defaultValue = "Hello World") String text) {

        EmbeddingResponse response =
                embeddingModel.embedForResponse(List.of(text));

        return Map.of(
                "text", text,
                "embedding", response
        );
    }
}