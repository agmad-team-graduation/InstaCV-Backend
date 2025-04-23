package com.Graduation.InstaCv.data.model.linkedin;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedinExperience {
    @Id
    @GeneratedValue
    private Long id;
    private String companyName;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private LinkedinProfile profile;
}
