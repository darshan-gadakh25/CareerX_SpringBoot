package com.careerx.serviceImpl;

import com.careerx.entities.*;
import com.careerx.repository.*;
import com.careerx.services.EmailService;
import com.careerx.services.GeminiAssessmentService;
import com.careerx.services.RoadmapService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final PaymentRepository paymentRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentSessionRepository assessmentSessionRepository;
    private final GeminiAssessmentService geminiAssessmentService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Map<String, Object> generateRoadmap(Long userId, Long paymentId) {
        // Check for subscription (valid for 2 years)
        Payment payment;
        if (paymentId != null) {
            payment = paymentRepository.findByIdAndStudentIdAndPaymentStatus(paymentId, userId, "Completed")
                    .orElseThrow(() -> new RuntimeException("Payment not found or not completed"));
        } else {
            // Check for existing valid subscription
            payment = paymentRepository.findTopByStudentIdAndPaymentTypeAndPaymentStatusOrderByCompletedAtDesc(
                    userId, "ROADMAP", "Completed")
                    .filter(p -> p.getCompletedAt().isAfter(java.time.LocalDateTime.now().minusYears(2)))
                    .orElseThrow(
                            () -> new RuntimeException("No valid roadmap subscription found. Please make a payment."));
        }

        // Always generate new roadmap if requested, but check if we need to return
        // existing one for this specific *request*?
        // Actually, if they paid simply to get access, we should generate one based on
        // latest assessment.
        // If they already have a roadmap for the *current* assessment, maybe return
        // that?
        // For simplicity, let's always generate a new one if they have a subscription,
        // OR we could check if a roadmap was already generated for this payment
        // RECENTLY?
        // But since one payment covers 2 years, we can't restrict by payment ID unique
        // constraint anymore.

        // We will just generate a new one.

        // Get student profile
        StudentProfile profile = studentProfileRepository.findByUserId(userId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Get latest assessment
        AssessmentSession assessment = assessmentSessionRepository.findAll().stream()
                .filter(as -> as.getUserId().equals(userId) && as.isCompleted())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No completed assessment found"));

        try {
            String profileJson = objectMapper.writeValueAsString(profile);
            String assessmentResult = assessment.getCareerReportJson();

            String roadmapDataJson = geminiAssessmentService.generateRoadmapAsync(profileJson,
                    assessmentResult);
            JsonNode root = objectMapper.readTree(roadmapDataJson);

            Roadmap roadmap = Roadmap.builder()
                    .student(payment.getStudent())
                    .payment(payment)
                    .careerOption(root.path("top3Careers").toString())
                    .roadmapJson(root.path("roadmaps").toString())
                    .roadmapHtml(root.path("htmlContent").asText())
                    .build();

            roadmapRepository.save(roadmap);

            // Send email
            try {
                // Use user from payment which is already fetched
                emailService.sendRoadmapEmail(payment.getStudent().getEmail(),
                        payment.getStudent().getName(),
                        roadmap.getRoadmapHtml());
            } catch (Exception e) {
                System.err.println("Failed to send roadmap email: " + e.getMessage());
            }

            return mapToResponse(roadmap);
        } catch (Exception e) {
            throw new RuntimeException("Error generating roadmap: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getMyRoadmaps(Long userId) {
        return roadmapRepository.findByStudentIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getRoadmap(Long userId, Long roadmapId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .filter(r -> r.getStudent().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));
        return mapToResponse(roadmap);
    }

    private Map<String, Object> mapToResponse(Roadmap roadmap) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("roadmapId", roadmap.getId());
            response.put("careerOptions", objectMapper.readTree(roadmap.getCareerOption()));
            response.put("roadmap", objectMapper.readTree(roadmap.getRoadmapJson()));
            response.put("htmlContent", roadmap.getRoadmapHtml());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping roadmap response", e);
        }
    }
}
