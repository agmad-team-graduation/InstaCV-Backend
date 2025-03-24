package com.Graduation.InstaCv.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAnalysis {
    @ElementCollection
    @CollectionTable(name = "jobs_hard_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<JobSkill> hardSkills;

    @ElementCollection
    @CollectionTable(name = "jobs_soft_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<JobSkill> softSkills;
}

