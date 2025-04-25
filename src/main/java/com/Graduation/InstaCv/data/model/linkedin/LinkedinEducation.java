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
public class LinkedinEducation {
    @Id
    @GeneratedValue
    private Long id;
    private String schoolName;
    private String degreeName;
    private LocalDate startDate;
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private LinkedinProfile profile;
}
