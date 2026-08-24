package com.privatedocsai.service;

import org.springframework.stereotype.Service;

@Service
public class AskServiceImpl implements AskService {

    @Override
    public String askQuestion(String question) {

        // Temporary response.
        // We will connect Gemini + document chunks in the next step.

        if (question.toLowerCase().contains("programming language")) {
            return "According to the uploaded document, please check the Skills or Technical Skills section for the programming languages.";
        }

        return "I received your question: " + question;
    }
}