package com.careerx.services;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOTPEmail(String toEmail, String otp);
   
    void sendRegistrationEmail(String to, String studentName) throws MessagingException;
}