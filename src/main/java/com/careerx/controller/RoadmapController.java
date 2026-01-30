package com.careerx.controller;

import com.careerx.entities.Roadmap;
import com.careerx.services.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateRoadmap(Authentication authentication, @RequestBody Map<String, Long> request) {
        Long userId = Long.parseLong(authentication.getName());
        Long paymentId = request.get("paymentId");
        return ResponseEntity.ok(roadmapService.generateRoadmap(userId, paymentId));
    }

    @GetMapping("/my-roadmaps")
    public ResponseEntity<List<Map<String, Object>>> getMyRoadmaps(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(roadmapService.getMyRoadmaps(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoadmap(Authentication authentication, @PathVariable Long id) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(roadmapService.getRoadmap(userId, id));
    }
}
