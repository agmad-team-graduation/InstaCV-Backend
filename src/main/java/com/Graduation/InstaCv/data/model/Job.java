package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.profile.Profile;
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
    private Profile profile;
    private String title;
    private String company;
    @Column(nullable = false, length = 2048)
    private String description;
    private Boolean isAnalyzed = false;
    private Boolean isMatchingAnalyzed = false;
    // This is the refactor of JobAnalysis object
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobSkill> jobSkills;
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
}