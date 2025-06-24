package com.Graduation.InstaCv.data.model.cv.items;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cv_education")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationCv implements CvItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String degree;
    private String school;
    private String city;
    private String country;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_present")
    private boolean isPresent;

    @Column(name = "is_hidden")
    @Builder.Default
    private boolean isHidden = false;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "order_index")
    private Integer orderIndex;
}
