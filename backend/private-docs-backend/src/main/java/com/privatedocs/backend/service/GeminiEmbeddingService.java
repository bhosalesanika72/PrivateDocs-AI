package com.privatedocs.backend.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiEmbeddingService {

    private final EmbeddingModel embeddingModel;

    public GeminiEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<Double> generateEmbedding(String text) {

        float[] vector = embeddingModel.embed(text);

        List<Double> embedding = new java.util.ArrayList<>();

        for (float value : vector) {
            embedding.add((double) value);
        }

        return embedding;
    }
}