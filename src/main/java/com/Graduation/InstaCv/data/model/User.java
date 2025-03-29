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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "is_profile_created")
    private boolean isProfileCreated = false;
    @Embedded
    private Profile profile;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
