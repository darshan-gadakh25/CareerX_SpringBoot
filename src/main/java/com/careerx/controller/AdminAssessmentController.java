package com.careerx.controller;

import com.careerx.entities.AssessmentSession;
import com.careerx.repository.AssessmentSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/AdminAssessment")
@RequiredArgsConstructor
public class AdminAssessmentController {

    private final AssessmentSessionRepository assessmentSessionRepository;

    @GetMapping
    // @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<AssessmentSession>> getAllAssessments() {
        return ResponseEntity.ok(assessmentSessionRepository.findAll());
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<AssessmentSession> getAssessment(@PathVariable Long id) {
        return assessmentSessionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
