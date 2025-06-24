package com.Graduation.InstaCv.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "user_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "photo_public_id", unique = true)
    private String photoPublicId;

    @Column(name = "photo_format")
    private String photoFormat;

    @Column(name = "photo_size")
    private Long photoSize;

    @Column(name = "uploaded_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedAt;

    @Column(name = "created_at",  updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    // Additional metadata fields
    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "cloudinary_version")
    private String cloudinaryVersion;
}