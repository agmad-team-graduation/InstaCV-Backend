package com.Graduation.InstaCv.utils;

import com.Graduation.InstaCv.config.FrontendProperties;
import com.Graduation.InstaCv.exceptions.EmailSendException;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailUtils {
    private JavaMailSender mailSender;
    private FrontendProperties frontendProps;

    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click the link below:\n\n" +
                    frontendProps.getResetUrl() + "?token=" +
                    resetToken + "\n\nThe link will expire in 30 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendException("Failed to send email", e);
        }
    }

    public void sendVerificationEmail(String name, String to, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Email Verification");
            message.setText("Hello, " + name + "\n" + "Welcome to InstaCv! Please verify your email address by clicking the link below:\n\n" +
                    frontendProps.getVerificationUrl() + "?name=" +
                    name + "&email=" + to + "&verificationToken=" + verificationToken + "\n\nThe link will expire in 24 hours.\n\n" +
                    "If you did not create an account with us, please ignore this email.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendException("Failed to send verification email", e);
        }
    }
}