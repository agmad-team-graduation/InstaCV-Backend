package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.enums.SkillType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id")
    @JsonIgnore
    @ToString.Exclude
    private Job job;

    @Column(name = "job_skill", nullable = false)
    private String skill;

    @Column(name = "model_confidence")
    private float modelConfidence;

    // To distinguish hard vs soft skill
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false)
    private SkillType skillType;
}
