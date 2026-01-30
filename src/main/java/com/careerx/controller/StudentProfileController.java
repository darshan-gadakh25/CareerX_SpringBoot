package com.careerx.controller;

import com.careerx.apirequests.StudentProfileRequest;
import com.careerx.services.StudentProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/StudentProfile")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody StudentProfileRequest request,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(studentProfileService.createStudentProfile(userId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.careerx.apiresponses.ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @GetMapping
    public ResponseEntity<?> get(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(studentProfileService.getStudentProfile(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new com.careerx.apiresponses.ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getById(@PathVariable Long studentId) {
        try {
            return ResponseEntity.ok(studentProfileService.getStudentProfileById(studentId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new com.careerx.apiresponses.ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody StudentProfileRequest request,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(studentProfileService.updateStudentProfile(userId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.careerx.apiresponses.ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            boolean deleted = studentProfileService.deleteStudentProfile(userId);
            if (deleted) {
                return ResponseEntity.ok()
                        .body(new com.careerx.apiresponses.ApiResponse<>("Student profile deleted successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new com.careerx.apiresponses.ApiResponse<>("Student profile not found", "Failed"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.careerx.apiresponses.ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @PostMapping("/picture")
    public ResponseEntity<?> uploadPicture(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            String url = studentProfileService.uploadProfilePicture(userId, file);
            return ResponseEntity.ok(java.util.Map.of("profilePictureUrl", url));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.careerx.apiresponses.ApiResponse<>(e.getMessage(), "Failed"));
        }
    }
}