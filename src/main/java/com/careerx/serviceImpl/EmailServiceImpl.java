package com.careerx.serviceImpl;

import com.careerx.services.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from}")
    private String fromEmail;

    @Override
    public void sendOTPEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("CareerX - OTP Verification");
        message.setText("Your OTP is: " + otp + "\n\nThis OTP will expire in 10 minutes.");

        mailSender.send(message);
    }

    public void sendRegistrationEmail(String to, String studentName) throws MessagingException {
        String subject = "Welcome to CareerX - Registration Successful!";
        String body = """
                <html>
                <body style='font-family: Arial, sans-serif; padding: 20px;'>
                    <div style='max-width: 600px; margin: 0 auto;'>
                        <h2 style='color: #2F4156;'>Welcome to CareerX, %s!</h2>
                        <p>Thank you for registering with CareerX. Your account has been successfully created.</p>
                        <p>To get started:</p>
                        <ol>
                            <li>Complete your student profile</li>
                            <li>Take the career assessment test</li>
                            <li>Receive personalized career recommendations</li>
                            <li>Subscribe to our career-path roadmap service</li>
                        </ol>
                        <p style='margin-top: 20px;'>
                            <a href='http://localhost:5173/login'
                               style='background-color: #2F4156; color: white; padding: 10px 20px;
                                      text-decoration: none; border-radius: 5px; display: inline-block;'>
                               Login to Your Dashboard
                            </a>
                        </p>
                        <p style='margin-top: 20px; color: #666;'>
                            If you have any questions, feel free to contact us.
                        </p>
                        <br/>
                        <p>Best regards,<br/>CareerX Team</p>
                    </div>
                </body>
                </html>
                """.formatted(studentName);

        sendEmail(to, subject, body);
    }

    @Override
    public void sendAssessmentReportEmail(String to, String studentName, byte[] pdfReport, double score)
            throws MessagingException {
        String subject = "Your CareerX Assessment Report - Score: " + String.format("%.1f", score) + "%";
        String body = """
                <html>
                <body style='font-family: Arial, sans-serif; padding: 20px;'>
                    <div style='max-width: 600px; margin: 0 auto;'>
                        <h2 style='color: #2F4156;'>Hello %s,</h2>
                        <p>Congratulations on completing your career assessment!</p>
                        <p>Your assessment score is: <strong>%.1f%%</strong></p>
                        <p>We have attached your detailed career recommendation report as a PDF to this email.</p>
                        <p>Our AI has analyzed your skills, interests, and educational background to provide these personalized recommendations.</p>
                        <p>What's next?</p>
                        <ul>
                            <li>Review your report and recommended career paths.</li>
                            <li>Explore different careers in our "Explore" section.</li>
                            <li>Get a detailed step-by-step roadmap for your chosen career.</li>
                        </ul>
                        <br/>
                        <p>Best regards,<br/>CareerX Team</p>
                    </div>
                </body>
                </html>
                """
                .formatted(studentName, score);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        // Attach PDF
        helper.addAttachment("CareerX_Assessment_Report.pdf",
                new org.springframework.core.io.ByteArrayResource(pdfReport));

        mailSender.send(message);
    }

    @Override
    public void sendRoadmapEmail(String to, String studentName, String roadmapHtml) throws MessagingException {
        String subject = "Your Personalized Career Roadmap from CareerX";
        String body = """
                <html>
                <body style='font-family: Arial, sans-serif; padding: 20px;'>
                    <div style='max-width: 800px; margin: 0 auto;'>
                        <h2 style='color: #2F4156;'>Hello %s,</h2>
                        <p>Your personalized career roadmap is ready!</p>
                        <div style='border: 1px solid #ddd; padding: 20px; border-radius: 10px; background-color: #f9f9f9;'>
                            %s
                        </div>
                        <p style='margin-top: 20px;'>
                            We hope this roadmap helps you achieve your career goals. If you need any further guidance, our AI Career Advisor is always here to help.
                        </p>
                        <br/>
                        <p>Best regards,<br/>CareerX Team</p>
                    </div>
                </body>
                </html>
                """
                .formatted(studentName, roadmapHtml);

        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = HTML

        mailSender.send(message);
    }
}
