package com.careerx.controller;

import com.careerx.entities.*;
import com.careerx.enums.UserRole;
import com.careerx.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/Admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentSessionRepository assessmentSessionRepository;
    private final PaymentRepository paymentRepository;
    private final BlogRepository blogRepository;
    private final ExploreCareerRepository exploreCareerRepository;

    @GetMapping("/students")
    // @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> getAllStudents() {
        List<Users> students = userRepository.findByRole(UserRole.STUDENT);

        List<Map<String, Object>> result = students.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("age", u.getAge());
            map.put("location", u.getLocation());
            map.put("profilePictureUrl", u.getProfilePictureUrl());
            map.put("hasProfile", !studentProfileRepository.findByUserId(u.getId()).isEmpty());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/students/{userId}")
    // @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> getStudentDetails(@PathVariable Long userId) {
        Users student = userRepository.findById(userId)
                .filter(u -> u.getRole() == UserRole.STUDENT)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(userId).stream()
                .findFirst()
                .orElse(null);

        List<AssessmentSession> assessments = assessmentSessionRepository.findAll().stream()
                .filter(a -> a.getUserId().equals(userId))
                .collect(Collectors.toList());

        List<Payment> payments = paymentRepository.findByStudentIdOrderByCreatedDateDesc(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("user", student);
        response.put("profile", profile);
        response.put("assessments", assessments);
        response.put("payments", payments);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    // @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", userRepository.findByRole(UserRole.STUDENT).size());
        stats.put("totalAssessments", assessmentSessionRepository.count());
        stats.put("completedAssessments",
                assessmentSessionRepository.findAll().stream().filter(AssessmentSession::isCompleted).count());
        stats.put("totalPayments",
                paymentRepository.findAll().stream().filter(p -> "Completed".equals(p.getPaymentStatus())).count());
        stats.put("totalRevenue", paymentRepository.findAll().stream()
                .filter(p -> "Completed".equals(p.getPaymentStatus())).mapToDouble(Payment::getAmount).sum());
        stats.put("totalBlogs", blogRepository.count());
        stats.put("totalCareers", exploreCareerRepository.count());

        return ResponseEntity.ok(stats);
    }
}
