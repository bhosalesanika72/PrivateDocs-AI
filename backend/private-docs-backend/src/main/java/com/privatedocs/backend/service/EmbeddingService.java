package com.privatedocs.backend.service;

public interface EmbeddingService {

    float[] generateEmbedding(String text);
}