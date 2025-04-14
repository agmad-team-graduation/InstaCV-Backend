package com.Graduation.InstaCv.data.model;


import com.Graduation.InstaCv.data.model.profile.UserSkill;
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
@Table(name = "matched_skills")
public class MatchedSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "analysis_id")
    private SkillMatchingAnalysis skillMatchingAnalysis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_skill_id")
    private JobSkill jobSkill;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_skill_id")
    private UserSkill userSkill;

    private float similarity;
}
