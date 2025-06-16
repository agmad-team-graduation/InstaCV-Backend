package com.Graduation.InstaCv.data.model.job;

import com.Graduation.InstaCv.data.enums.AnalyzeStatus;
import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.jobMatching.projectMatching.ProjectsMatchingAnalysis;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.SkillMatchingAnalysis;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "profile_jobs")
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
    private String summary;
    @Column(nullable = false, length = 20480)
    private String description;
    @Builder.Default
    private AnalyzeStatus skillExtractionStatus = AnalyzeStatus.NOT_STARTED;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<JobSkill> jobSkills = List.of();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SkillMatchingAnalysis> skillMatchingAnalyses = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectsMatchingAnalysis> projectMatchingAnalyses = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private RemoteJobData remoteJobData;


    private OffsetDateTime addDate;

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