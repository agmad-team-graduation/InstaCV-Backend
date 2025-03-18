package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.model.profile.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Embedded
    private Profile profile;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
