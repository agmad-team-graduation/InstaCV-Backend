package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.model.profile.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "project_skill", nullable = false)
    private String skill;
}
