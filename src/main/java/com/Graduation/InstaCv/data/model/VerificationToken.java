package com.Graduation.InstaCv.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    private boolean used = false;

    public VerificationToken(String email, String tokenValue, int hours) {
        this.email = email;
        this.token = tokenValue;
        this.expiryDate = LocalDateTime.now().plusHours(hours);
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}