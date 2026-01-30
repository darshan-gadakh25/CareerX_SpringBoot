package com.careerx.controller;

import com.careerx.services.StudentAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/StudentAssessment")
@RequiredArgsConstructor
public class StudentAssessmentController {

    private final StudentAssessmentService studentAssessmentService;

    @GetMapping("/available")
    public ResponseEntity<?> getAvailable(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(studentAssessmentService.checkAvailability(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    @PostMapping("/start")
    public ResponseEntity<?> startAssessment(Authentication authentication,
            @RequestBody(required = false) Map<String, Long> request) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            Long paymentId = request != null ? request.get("paymentId") : null;
            return ResponseEntity.ok(studentAssessmentService.startAssessment(userId, paymentId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error starting assessment: " + e.getMessage()));
        }
    }

    @PostMapping("/submit/{studentAssessmentId}")
    public ResponseEntity<?> submitAssessment(
            @PathVariable Long studentAssessmentId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(studentAssessmentService.submitAssessment(studentAssessmentId, request, userId));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }
            if (e.getMessage().equals("Assessment already submitted.")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error submitting assessment: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error submitting assessment: " + e.getMessage()));
        }
    }

    @GetMapping("/my-assessments")
    public ResponseEntity<?> getMyAssessments(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(studentAssessmentService.getMyAssessments(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/report/{studentAssessmentId}")
    public ResponseEntity<?> getReport(
            @PathVariable Long studentAssessmentId,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(studentAssessmentService.getReport(studentAssessmentId, userId));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }
            if (e.getMessage().equals("Report not ready or session missing.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching report: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching report: " + e.getMessage()));
        }
    }
}
