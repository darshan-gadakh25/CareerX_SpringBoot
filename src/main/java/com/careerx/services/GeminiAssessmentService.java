package com.careerx.services;


public interface GeminiAssessmentService {
    String generate60QuestionsAsync(String profileJson);

    String evaluateResultsAsync(String questionsJson, String answersJson, String profileJson);

    String generateRoadmapAsync(String profileJson, String assessmentResultJson);

    String chat(String message);
}
