package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.auth.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
