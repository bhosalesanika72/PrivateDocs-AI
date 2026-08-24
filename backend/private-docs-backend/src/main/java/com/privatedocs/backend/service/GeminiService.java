package com.privatedocs.backend.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        System.out.println("Creating Gemini Client...");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Gemini API key is missing. Add gemini.api.key to application.properties."
            );
        }

        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

        System.out.println("Gemini Client created successfully.");
    }

    public String generateAnswer(String question, String context) {

        String prompt = """
                You are PrivateDocs AI, an assistant that answers questions
                only from the provided document.

                DOCUMENT CONTENT:
                %s

                USER QUESTION:
                %s

                INSTRUCTIONS:
                - Answer using only the document content.
                - If the answer is not present in the document, say:
                  "I could not find this information in the document."
                - Do not invent information.
                - Give a clear and concise answer.

                ANSWER:
                """.formatted(context, question);

        System.out.println("Sending request to Gemini...");

        try {

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.7-flash",
                            prompt,
                            null
                    );

            System.out.println("Gemini response received.");

            String answer = response.text();

            if (answer == null || answer.isBlank()) {
                return "I could not generate an answer.";
            }

            return answer;

        } catch (Exception e) {

            System.err.println("Gemini API error:");
            e.printStackTrace();

            return "Unable to generate an AI answer: " + e.getMessage();
        }
    }
}