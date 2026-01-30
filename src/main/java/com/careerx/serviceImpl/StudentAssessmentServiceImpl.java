package com.careerx.serviceImpl;

import com.careerx.entities.AssessmentSession;
import com.careerx.entities.Payment;
import com.careerx.entities.StudentProfile;
import com.careerx.entities.Users;
import com.careerx.repository.AssessmentSessionRepository;
import com.careerx.repository.PaymentRepository;
import com.careerx.repository.StudentProfileRepository;
import com.careerx.repository.UserRepository;
import com.careerx.services.EmailService;
import com.careerx.services.GeminiAssessmentService;
import com.careerx.services.PdfService;
import com.careerx.services.StudentAssessmentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAssessmentServiceImpl implements StudentAssessmentService {

    private final AssessmentSessionRepository assessmentSessionRepository;
    private final PaymentRepository paymentRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final GeminiAssessmentService geminiAssessmentService;
    private final PdfService pdfService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> checkAvailability(Long userId) {
        // Check profile
        boolean hasProfile = !studentProfileRepository.findByUserId(userId).isEmpty();
        if (!hasProfile) {
            return Map.of("canTakeAssessment", false, "message", "Please complete your profile first.");
        }

        List<AssessmentSession> sessions = assessmentSessionRepository.findByUserId(userId);

        boolean hasCompleted = sessions.stream().anyMatch(AssessmentSession::isCompleted);
        // Removed blocking check for hasCompleted to allow retakes

        List<AssessmentSession> inProgressList = assessmentSessionRepository.findByUserIdAndIsCompletedFalse(userId);
        AssessmentSession inProgress = inProgressList.isEmpty() ? null : inProgressList.get(0);

        if (inProgress != null) {
            return Map.of(
                    "canTakeAssessment", true,
                    "hasInProgress", true,
                    "studentAssessmentId", inProgress.getId());
        }

        // Allow retakes (will require payment in startAssessment if hasCompleted is
        // true)
        return Map.of("canTakeAssessment", true, "hasInProgress", false, "hasCompleted", hasCompleted);
    }

    @Override
    public Map<String, Object> startAssessment(Long userId, Long paymentId) throws Exception {
        StudentProfile profile = studentProfileRepository.findByUserId(userId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student profile not found."));

        // Check if there is an in-progress session (Resume)
        List<AssessmentSession> existingList = assessmentSessionRepository.findByUserIdAndIsCompletedFalse(userId);
        AssessmentSession existing = existingList.isEmpty() ? null : existingList.get(0);

        if (existing != null) {
            // Convert JsonNode to List for proper serialization
            List<Map<String, Object>> existingQuestions = objectMapper.convertValue(
                    objectMapper.readTree(existing.getQuestionsJson()),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            return Map.of(
                    "studentAssessmentId", existing.getId(),
                    "questions", existingQuestions,
                    "durationMinutes", 60,
                    "webcamRequired", true);
        }

        // Logic for New Assessment
        List<AssessmentSession> completedSessions = assessmentSessionRepository.findByUserId(userId).stream()
                .filter(AssessmentSession::isCompleted)
                .collect(Collectors.toList());

        Payment payment = null;
        if (!completedSessions.isEmpty()) {
            // Subsequent assessment: Require Payment
            if (paymentId == null) {
                throw new RuntimeException("Payment required for additional assessments.");
            }

            payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            if (!payment.getStudent().getId().equals(userId)) {
                throw new RuntimeException("Invalid payment user");
            }
            if (!"Completed".equals(payment.getPaymentStatus())) {
                throw new RuntimeException("Payment not completed");
            }
            if (!"ASSESSMENT".equals(payment.getPaymentType())) {
                throw new RuntimeException("Invalid payment type");
            }

            // Check if payment already used
            if (assessmentSessionRepository.findByPaymentId(paymentId).isPresent()) {
                throw new RuntimeException("Payment already used for another assessment");
            }
        }
        // If first assessment (completedSessions.isEmpty()), allow free (payment =
        // null)

        // Generate new questions
        String profileJson = objectMapper.writeValueAsString(profile);
        String questions = geminiAssessmentService.generate60QuestionsAsync(profileJson);

        AssessmentSession session = AssessmentSession.builder()
                .userId(userId)
                .questionsJson(questions)
                .payment(payment)
                .build();

        assessmentSessionRepository.save(session);

        // Convert JsonNode to List for proper serialization
        List<Map<String, Object>> questionsList = objectMapper.convertValue(
                objectMapper.readTree(questions),
                new TypeReference<List<Map<String, Object>>>() {
                });

        return Map.of(
                "studentAssessmentId", session.getId(),
                "questions", questionsList,
                "durationMinutes", 60,
                "webcamRequired", true);
    }

    @Override
    public Map<String, Object> submitAssessment(Long studentAssessmentId, Map<String, Object> request, Long userId)
            throws Exception {
        AssessmentSession session = assessmentSessionRepository.findById(studentAssessmentId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (session.isCompleted()) {
            throw new RuntimeException("Assessment already submitted.");
        }

        List<Integer> answers = objectMapper.convertValue(request.get("answers"),
                new TypeReference<List<Integer>>() {
                });
        String answersJson = objectMapper.writeValueAsString(answers);
        String webcamUrl = (String) request.get("webcamRecordingUrl");

        StudentProfile profile = studentProfileRepository.findByUserId(userId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student profile not found."));
        String profileJson = objectMapper.writeValueAsString(profile);

        // Calculate score
        JsonNode questionsNode = objectMapper.readTree(session.getQuestionsJson());
        int correctCount = 0;
        int totalQuestions = questionsNode.size();

        for (int i = 0; i < totalQuestions; i++) {
            if (i < answers.size()) {
                JsonNode q = questionsNode.get(i);
                int correctIdx = q.path("correctOptionIndex").asInt(-1);
                if (correctIdx == -1) {
                    correctIdx = q.path("CorrectOptionIndex").asInt(-1);
                }

                if (correctIdx == answers.get(i)) {
                    correctCount++;
                }
            }
        }

        double score = (double) correctCount * 100 / totalQuestions;

        // Call AI to evaluate
        String report = geminiAssessmentService.evaluateResultsAsync(
                session.getQuestionsJson(),
                answersJson,
                profileJson);

        session.setUserAnswersJson(answersJson);
        session.setCareerReportJson(report);
        session.setWebcamRecordingUrl(webcamUrl);
        session.setScore(score);
        session.setCompleted(true);

        assessmentSessionRepository.save(session);

        // Generate PDF and send email in background
        try {
        	 Users user = userRepository.findById(userId).orElse(null);
            JsonNode recommendationNode = objectMapper.readTree(report);
            byte[] pdfReport = pdfService.generateAssessmentReportPdf(session, recommendationNode, user.getName() );

           
            if (user != null) {
                emailService.sendAssessmentReportEmail(user.getEmail(), user.getName(), pdfReport, score);
            }
        } catch (Exception e) {
            System.err.println("Failed to send assessment report email: " + e.getMessage());
        }

        return Map.of(
                "studentAssessmentId", session.getId(),
                "score", score,
                "recommendation", objectMapper.readTree(report),
                "message", "Assessment submitted successfully. Report has been sent to your email.");
    }

    @Override
    public List<Map<String, Object>> getMyAssessments(Long userId) {
        List<AssessmentSession> sessions = assessmentSessionRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return sessions.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("studentAssessmentId", s.getId());
            map.put("isCompleted", s.isCompleted());
            map.put("startedAt", s.getCreatedAt());
            map.put("score", s.getScore());
            try {
                map.put("recommendation",
                        s.getCareerReportJson() != null ? objectMapper.readTree(s.getCareerReportJson()) : null);
            } catch (Exception e) {
                map.put("recommendation", null);
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getReport(Long studentAssessmentId, Long userId) throws Exception {
        AssessmentSession session = assessmentSessionRepository.findById(studentAssessmentId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (!session.isCompleted() || session.getCareerReportJson() == null) {
            throw new RuntimeException("Report not ready or session missing.");
        }

        return Map.of(
                "studentAssessmentId", session.getId(),
                "score", session.getScore() != null ? session.getScore() : 0.0,
                "recommendation", objectMapper.readTree(session.getCareerReportJson()),
                "completedAt", session.getCreatedAt());
    }
}
