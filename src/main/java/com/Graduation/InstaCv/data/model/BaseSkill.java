package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.model.profile.ProjectSkill;
import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.profile.UserSkill;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String skill;

    public JobSkill asJobSkill() {
        return JobSkill.builder()
                .id(this.id)
                .skill(this.skill)
                .build();
    }

    public UserSkill asUserSkill() {
        return UserSkill.builder()
                .id(this.id)
                .skill(this.skill)
                .build();
    }

    public ProjectSkill asProjectSkill() {
        return ProjectSkill.builder()
                .id(this.id)
                .skill(this.skill)
                .build();
    }
}
