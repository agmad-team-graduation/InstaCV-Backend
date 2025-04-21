package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.enums.SkillType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "job_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobSkill extends BaseSkill {
    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Job job;

    @Column(name = "model_confidence")
    private float modelConfidence;

    // To distinguish hard vs soft skill
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false)
    private SkillType skillType;
}
