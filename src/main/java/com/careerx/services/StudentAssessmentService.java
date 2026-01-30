package com.careerx.services;

import java.util.List;
import java.util.Map;

public interface StudentAssessmentService {

    Map<String, Object> checkAvailability(Long userId);

    Map<String, Object> startAssessment(Long userId, Long paymentId) throws Exception;

    Map<String, Object> submitAssessment(Long studentAssessmentId, Map<String, Object> request, Long userId)
            throws Exception;

    List<Map<String, Object>> getMyAssessments(Long userId);

    Map<String, Object> getReport(Long studentAssessmentId, Long userId) throws Exception;
}
