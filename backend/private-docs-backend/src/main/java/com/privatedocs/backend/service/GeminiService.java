package com.privatedocs.backend.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Gemini API key is missing. Check gemini.api.key in application.properties"
            );
        }

        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

        System.out.println("Gemini Client created successfully.");
    }

    public String generateAnswer(String question, String context) {

        if (question == null || question.isBlank()) {
            return "Please enter a question.";
        }

        if (context == null || context.isBlank()) {
            return "I could not find any document content to answer from.";
        }

        String prompt = """
                You are PrivateDocs AI.

                Answer the user's question ONLY using the document content
                provided below.

                DOCUMENT CONTENT:
                %s

                USER QUESTION:
                %s

                RULES:
                - Use only the document content.
                - Do not invent information.
                - If the answer is not present in the document, say:
                  "I could not find this information in the document."
                - Keep the answer clear and concise.

                ANSWER:
                """.formatted(context, question);

        try {

            System.out.println("Sending request to Gemini...");

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            prompt,
                            null
                    );

            String answer = response.text();

            if (answer == null || answer.isBlank()) {
                return "I could not generate an answer.";
            }

            return answer;

        } catch (Exception e) {

            System.err.println("Gemini API error: " + e.getMessage());

            return "Unable to generate an AI answer right now. Please try again.";
        }
    }
}