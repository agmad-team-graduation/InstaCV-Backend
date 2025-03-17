package com.Graduation.InstaCv.data.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String company;
    @Column(nullable = false, length = 2048)
    private String description;
    private boolean isAnalyzed = false;
    @Embedded
    private JobAnalysis jobAnalysis;
}