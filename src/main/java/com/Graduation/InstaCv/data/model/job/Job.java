package com.Graduation.InstaCv.data.model.job;

import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.jobMatching.projectMatching.ProjectsMatchingAnalysis;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.SkillMatchingAnalysis;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "profile_jobs")
@ToString(exclude = {"profile", "jobSkills", "skillMatchingAnalysis", "projectMatchingAnalysis", "remoteJobData"})
@EqualsAndHashCode(exclude = {"profile", "jobSkills", "skillMatchingAnalysis", "projectMatchingAnalysis", "remoteJobData"})
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private Profile profile;
    private String title;
    private String company;
    @Column(nullable = false, length = 20480)
    private String description;
    private boolean isAnalyzed = false;
    private boolean isSkillMatchingAnalyzed = false;
    private boolean isProjectMatchingAnalyzed = false;
    private boolean analyzeFailed = false;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<JobSkill> jobSkills = List.of();

    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private SkillMatchingAnalysis skillMatchingAnalysis;

    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectsMatchingAnalysis projectMatchingAnalysis;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private RemoteJobData remoteJobData;

    public List<JobSkill> getHardSkills() {
        return jobSkills.stream()
                .filter(jobSkill -> jobSkill.getSkillType() == SkillType.HARD)
                .toList();
    }

    public List<JobSkill> getSoftSkills() {
        return jobSkills.stream()
                .filter(jobSkill -> jobSkill.getSkillType() == SkillType.SOFT)
                .toList();
    }
}