package com.careerx.controller;

import com.careerx.services.GeminiAssessmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/Chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final GeminiAssessmentService geminiAssessmentService;
    private final ObjectMapper objectMapper;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String responseJson = geminiAssessmentService.chat(message);
            return ResponseEntity.ok(objectMapper.readTree(responseJson));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Chat error: " + e.getMessage());
        }
    }
}
