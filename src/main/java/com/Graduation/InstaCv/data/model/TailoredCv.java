package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.model.profile.Education;
import com.Graduation.InstaCv.data.model.profile.Experience;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
import com.Graduation.InstaCv.data.model.profile.UserSkill;
import com.Graduation.InstaCv.data.model.profile.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tailored_cvs")
public class TailoredCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Embedded
    private PersonalDetails personalDetails;

    @ManyToMany
    @JoinTable(
            name = "tailored_cv_education",
            joinColumns = @JoinColumn(name = "tailored_cv_id"),
            inverseJoinColumns = @JoinColumn(name = "education_id")
    )
    private List<Education> education;

    @ManyToMany
    @JoinTable(
            name = "tailored_cv_experience",
            joinColumns = @JoinColumn(name = "tailored_cv_id"),
            inverseJoinColumns = @JoinColumn(name = "experience_id")
    )
    private List<Experience> experience;

    @ManyToMany
    @JoinTable(
            name = "tailored_cv_skills",
            joinColumns = @JoinColumn(name = "tailored_cv_id"),
            inverseJoinColumns = @JoinColumn(name = "user_skill_id")
    )
    private List<UserSkill> skills;

    @ManyToMany
    @JoinTable(
            name = "tailored_cv_projects",
            joinColumns = @JoinColumn(name = "tailored_cv_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects;

    private String summary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}