package com.Graduation.InstaCv.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "jobs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String title;
    String company;
    @Column(nullable = false)
    String description;
}
