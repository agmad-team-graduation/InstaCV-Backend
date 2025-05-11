package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.VerificationToken;
import com.Graduation.InstaCv.exceptions.EmailAlreadyExistsException;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.repository.VerificationTokenRepository;
import com.Graduation.InstaCv.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailUtils emailUtils;

    @Transactional
    public void sendVerificationEmail(String name ,String email) {
        // Check if user exists
        if (userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("Email " + email + " is already registered");
        }

        // Delete any existing tokens for this email
        tokenRepository.findByEmail(email).ifPresent(token ->
                tokenRepository.deleteById(token.getId())
        );

        // Create new verification token
        String tokenValue = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(name, email, tokenValue, 24); // 24 hours expiry
        tokenRepository.save(verificationToken);

        // Send verification email
        emailUtils.sendVerificationEmail(name ,email, tokenValue);
    }


    @Transactional
    public void verifyEmail(String token) {
        // Find the token
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid verification token"));

        // Check if token is expired
        if (verificationToken.isExpired()) {
            throw new IllegalStateException("Verification token has expired");
        }

        // Check if token is already used
        if (verificationToken.isUsed()) {
            throw new IllegalStateException("Verification token has already been used");
        }

        // Mark token as used
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }
}