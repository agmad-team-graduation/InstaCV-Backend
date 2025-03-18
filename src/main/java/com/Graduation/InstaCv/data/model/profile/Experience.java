package com.Graduation.InstaCv.data.model.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Experience {
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
    private String description;
}
