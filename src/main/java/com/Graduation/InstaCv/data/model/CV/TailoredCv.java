package com.Graduation.InstaCv.data.model.CV;

import com.Graduation.InstaCv.data.model.CV.skills.UserSkillCv;
import com.Graduation.InstaCv.data.model.Job;
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
@Table(name = "tailored_cvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private String summary;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EducationCv> education;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExperienceCv> experience;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSkillCv> skills;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectCv> projects;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
