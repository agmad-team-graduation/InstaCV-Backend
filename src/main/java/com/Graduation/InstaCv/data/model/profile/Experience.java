package com.Graduation.InstaCv.data.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "user_experience")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id")
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Profile profile;

    @Column(name = "job_title")
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
    @Column(length = 20480)
    private String description;
}

