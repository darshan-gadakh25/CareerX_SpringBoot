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

    private void sendEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = HTML

        mailSender.send(message);
    }
    
}
