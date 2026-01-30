package com.careerx.services;

import com.careerx.entities.AssessmentSession;
import com.fasterxml.jackson.databind.JsonNode;

public interface PdfService {
    byte[] generateAssessmentReportPdf(AssessmentSession session, JsonNode recommendation,  String  userName);
}
