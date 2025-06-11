package com.Graduation.InstaCv.data.model;

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
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    private boolean used = false;

    public VerificationToken(String name ,String email, String tokenValue, int hours) {
        this.name = name;
        this.email = email;
        this.token = tokenValue;
        this.expiryDate = LocalDateTime.now().plusHours(hours);
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}