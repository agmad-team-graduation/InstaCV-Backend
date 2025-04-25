package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean processForgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Generate unique token
            String token = UUID.randomUUID().toString();

            // Set token expiry (30 minutes from now)
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

            user.setResetToken(token);
            user.setResetTokenExpiryDate(expiryDate);
            userRepository.save(user);

            // Send email with reset link
            emailService.sendPasswordResetEmail(user.getEmail(), token);

            return true;
        }

        return false;
    }

    public boolean validateResetToken(String token) {
        Optional<User> userOptional = userRepository.findByResetToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if token is expired
            if (user.getResetTokenExpiryDate().isAfter(LocalDateTime.now())) {
                return true;
            }
        }

        return false;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Validate token expiry
            if (user.getResetTokenExpiryDate().isAfter(LocalDateTime.now())) {
                // Update password
                user.setPassword(passwordEncoder.encode(newPassword));

                // Clear reset token fields
                user.setResetToken(null);
                user.setResetTokenExpiryDate(null);

                userRepository.save(user);
                return true;
            }
        }

        return false;
    }
}