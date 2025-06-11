package com.Graduation.InstaCv.data.model.job;

import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.BaseSkill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true, exclude = {"job"})
@Entity
@Table(name = "job_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"job"})
public class JobSkill extends BaseSkill {
    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id")
    @JsonIgnore
    private Job job;

    @JsonIgnore
    @Transient
    private float modelConfidence;

    // To distinguish hard vs soft skill
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false)
    private SkillType skillType;
}