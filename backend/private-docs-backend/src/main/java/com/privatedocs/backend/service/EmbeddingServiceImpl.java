package com.privatedocs.backend.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingServiceImpl(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] generateEmbedding(String text) {

        return embeddingModel.embed(text);
    }
}