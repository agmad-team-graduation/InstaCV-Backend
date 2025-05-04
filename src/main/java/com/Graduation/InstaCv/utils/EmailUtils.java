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
}
