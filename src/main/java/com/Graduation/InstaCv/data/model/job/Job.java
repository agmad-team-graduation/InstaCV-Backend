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
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id")
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Profile profile;
    private String title;
    private String company;
    @Column(nullable = false, length = 2048)
    private String description;
    private boolean isAnalyzed = false;
    private boolean isSkillMatchingAnalyzed = false;
    private boolean isProjectMatchingAnalyzed = false;
    private boolean initialAnalyzeFailed = false;
    // This is the refactor of JobAnalysis object
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<JobSkill> jobSkills = List.of();
    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private SkillMatchingAnalysis skillMatchingAnalysis;
    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectsMatchingAnalysis projectMatchingAnalysis;
//    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TailoredCv> tailoredCvs;

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