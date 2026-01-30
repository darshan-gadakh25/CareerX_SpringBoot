package com.careerx.services;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOTPEmail(String toEmail, String otp);

    void sendRegistrationEmail(String to, String studentName) throws MessagingException;

    void sendAssessmentReportEmail(String to, String studentName, byte[] pdfReport, double score)
            throws MessagingException;

    void sendRoadmapEmail(String to, String studentName, String roadmapHtml) throws MessagingException;
}