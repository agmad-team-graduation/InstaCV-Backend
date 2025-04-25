package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.config.FrontendProperties;
import com.Graduation.InstaCv.exceptions.EmailSendException;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    private JavaMailSender mailSender;
    private FrontendProperties frontendProps;

    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click the link below:\n\n" +
                    frontendProps.getResetUrl() + resetToken + "\n\nThe link will expire in 30 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendException("Failed to send email", e);
        }
    }
}
