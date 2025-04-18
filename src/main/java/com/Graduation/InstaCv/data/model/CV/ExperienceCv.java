package com.Graduation.InstaCv.data.model.CV;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cv_experience")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;
    private String company;
    private String city;
    private String country;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_present")
    private boolean isPresent;

    private String description;

    @ManyToOne
    @JoinColumn(name = "tailored_cv_id")
    @ToString.Exclude
    @JsonIgnore
    private TailoredCv cv;
}
