package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByEmail(String email);
    Iterable<VerificationToken> findAllByEmail(String email);
}