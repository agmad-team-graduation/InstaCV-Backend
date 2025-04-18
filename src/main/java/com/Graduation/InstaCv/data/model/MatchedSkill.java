package com.Graduation.InstaCv.data.model;


import com.Graduation.InstaCv.data.model.profile.UserSkill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


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
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SkillMatchingAnalysis skillMatchingAnalysis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_skill_id")
    private JobSkill jobSkill;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_skill_id")
    private UserSkill userSkill;

    private float similarity;
}
