package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.model.profile.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "project_skills")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProjectSkill extends BaseSkill {
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;
}
