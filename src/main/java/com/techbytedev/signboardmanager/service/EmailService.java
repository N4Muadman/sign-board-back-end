package com.techbytedev.signboardmanager.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(
            "<h1>Password Reset</h1>" +
            "<p>You have requested to reset your password. Click the link below to reset it:</p>" +
            "<a href=\"" + resetLink + "\">Reset Password</a>" +
            "<p>This link will expire in 15 minutes.</p>" +
            "<p>If you did not request this, please ignore this email.</p>",
            true
        );

        mailSender.send(message);
    }
}