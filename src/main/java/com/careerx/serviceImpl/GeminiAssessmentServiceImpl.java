package com.careerx.serviceImpl;

import com.careerx.services.GeminiAssessmentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiAssessmentServiceImpl implements GeminiAssessmentService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Override
    public String generate60QuestionsAsync(String profileJson) {
        String prompt = """
                Generate 60 Multiple Choice Questions (MCQs) for a student assessment based on the following student profile:
                %s

                The questions should cover 3 categories:
                1. Logical Reasoning (20 questions)
                2. Technical/Academic Skills relevant to their education/interests (20 questions)
                3. Career & Soft Skills (20 questions)

                Format the output as a strictly valid JSON array of objects. Do not include markdown formatting (like ```json ... ```) in the response.
                Each object must have:
                - "QuestionText": string
                - "Options": array of 4 strings
                - "CorrectOptionIndex": integer (0-3)
                - "Category": string

                Ensure the questions are challenging and appropriate for the student's level.
                """
                .formatted(profileJson);

        return callGeminiApi(prompt);
    }

    @Override
    public String evaluateResultsAsync(String questionsJson, String answersJson, String profileJson) {
        String prompt = """
                Analyze the following student assessment results and provide career recommendations.

                Student Profile:
                %s

                Questions:
                %s

                Student Answers (indices):
                %s

                Please provide a comprehensive analysis in strict JSON format (no markdown). The JSON should have the following structure:
                {
                    "recommendedCareer": "Primary Career Path Recommendation",
                    "skillScore": 85 (integer 0-100 based on correct answers and difficulty),
                    "strengths": ["Strength 1", "Strength 2"],
                    "weaknesses": ["Weakness 1", "Weakness 2"],
                    "careerRoadmap": {
                        "shortTerm": "Steps for next 6 months",
                        "mediumTerm": "Steps for 1-2 years",
                        "longTerm": "5 year goal"
                    },
                    "suggestedCourses": ["Course 1", "Course 2"],
                    "suggestedColleges": ["College 1", "College 2"]
                }
                """
                .formatted(profileJson, questionsJson, answersJson);

        return callGeminiApi(prompt);
    }

    @Override
    public String generateRoadmapAsync(String profileJson, String assessmentResultJson) {
        String prompt = """
                You are an expert Career Counselor. Based on the following student profile and assessment results, generate a comprehensive career roadmap.

                Student Profile: %s
                Assessment Results: %s

                Generate a detailed roadmap for the TOP 3 BEST career options based on the assessment.

                Return a JSON object with this structure:
                {
                  "top3Careers": [
                    {
                      "careerName": "Career Name",
                      "fitScore": 95,
                      "whyFit": "Explanation"
                    }
                  ],
                  "roadmaps": [
                    {
                      "careerName": "Career Name",
                      "phases": [
                        {
                          "phaseName": "Foundation Phase",
                          "duration": "Months 1-6",
                          "steps": ["Step 1", "Step 2"],
                          "skills": ["Skill 1", "Skill 2"],
                          "certifications": ["Cert 1"],
                          "resources": ["Resource 1"]
                        }
                      ],
                      "jobMarketOutlook": "Description",
                      "salaryRange": "Range",
                      "timeline": "24 months"
                    }
                  ],
                  "htmlContent": "Formatted HTML version of the roadmap"
                }

                Return ONLY valid JSON.
                """
                .formatted(profileJson, assessmentResultJson);

        return callGeminiApi(prompt);
    }

    @Override
    public String chat(String message) {
        String prompt = """
                You are a helpful AI career guidance assistant for CareerX platform.
                You help students with career-related questions, provide guidance on assessments, roadmaps, and career planning.

                User Question: %s

                Provide a helpful, concise, and professional response. If the question is not career-related, politely redirect to career topics.

                Return a JSON object with this structure:
                {
                  "response": "Your response text",
                  "suggestions": ["Suggestion 1", "Suggestion 2"]
                }

                Return ONLY valid JSON.
                """
                .formatted(message);

        return callGeminiApi(prompt);
    }

    private String callGeminiApi(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();

            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", new Object[] { part });

            requestBody.put("contents", new Object[] { content });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = apiUrl + "?key=" + apiKey;
            System.out.println("Calling Gemini API: " + url);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String result = extractTextFromGeminiResponse(response.getBody());
                if (result == null || result.isEmpty()) {
                    throw new RuntimeException(
                            "Gemini returned an empty response. Response body: " + response.getBody());
                }
                return result;
            } else {
                System.err.println("Gemini API Error Body: " + response.getBody());
                throw new RuntimeException(
                        "Gemini API call failed: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String errorBody = e.getResponseBodyAsString();
            System.err.println("Gemini API Status Code: " + e.getStatusCode());
            System.err.println("Gemini API Error Response: " + errorBody);
            throw new RuntimeException("Gemini API error: " + e.getStatusCode() + " - " + errorBody);
        } catch (Exception e) {
            System.err.println("Unexpected error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error calling Gemini API: " + e.getMessage(), e);
        }
    }

    private String extractTextFromGeminiResponse(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText();
                    return cleanupJson(text);
                }
            }
            return "";
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Gemini response", e);
        }
    }

    private String cleanupJson(String text) {
        text = text.trim();
        if (text.startsWith("```json")) {
            text = text.substring(7);
        } else if (text.startsWith("```")) {
            text = text.substring(3);
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }
        return text.trim();
    }
}
