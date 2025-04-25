package com.Graduation.InstaCv.data.model.linkedin;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private LinkedinProfile profile;
}
