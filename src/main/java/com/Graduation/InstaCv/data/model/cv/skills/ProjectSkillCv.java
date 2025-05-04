package com.Graduation.InstaCv.data.model.cv.skills;

import com.Graduation.InstaCv.data.model.cv.items.ProjectCv;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tailored_cv_project_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSkillCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String skill;

    @ManyToOne
    @JoinColumn(name = "project_cv_id")
    @ToString.Exclude
    @JsonIgnore
    private ProjectCv projectCv;
}
