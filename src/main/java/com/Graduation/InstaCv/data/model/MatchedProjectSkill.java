package com.Graduation.InstaCv.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "matched_project_skills")
public class MatchedProjectSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "matched_project_id")
    private MatchedProject matchedProject;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_skill_id")
    private JobSkill jobSkill;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_skill_id")
    private ProjectSkill projectSkill;

    private Float similarity;
}
