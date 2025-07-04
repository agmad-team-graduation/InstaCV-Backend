package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.enums.AuthProvider;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.auth.PasswordResetToken;
import com.Graduation.InstaCv.exceptions.InvalidTokenException;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.PasswordResetTokenRepository;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.IPasswordResetService;
import com.Graduation.InstaCv.utils.EmailUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetService implements IPasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailUtils emailUtils;
    private final PasswordEncoder passwordEncoder;

    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Check if user is an OAuth user
        if (user.getAuthProvider() != null && user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new InvalidTokenException("Password reset is not available for social login accounts. Please use the social login option.");
        }

        PasswordResetToken token = PasswordResetToken.builder()
                // Generate unique token
                .token(UUID.randomUUID().toString())
                // Set token expiry (30 minutes from now)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();

        Optional<PasswordResetToken> oldToken = passwordResetTokenRepository.findByUserId(user.getId());
        oldToken.ifPresent(passwordResetTokenRepository::delete);

        passwordResetTokenRepository.save(token);
        emailUtils.sendPasswordResetEmail(user.getEmail(), token.getToken());
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken tokenEntity = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid Password Reset Token"));
        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(tokenEntity);
            throw new InvalidTokenException("Password Reset Token has expired");
        }
        User user = tokenEntity.getUser();
        
        // Check if user is an OAuth user (additional safety check)
        if (user.getAuthProvider() != null && user.getAuthProvider() != AuthProvider.LOCAL) {
            passwordResetTokenRepository.delete(tokenEntity);
            throw new InvalidTokenException("Password reset is not available for social login accounts. Please use the social login option.");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(tokenEntity);
    }

}