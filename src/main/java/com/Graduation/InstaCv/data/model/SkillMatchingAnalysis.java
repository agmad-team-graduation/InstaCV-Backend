package com.Graduation.InstaCv.data.model;


import com.Graduation.InstaCv.data.model.profile.UserSkill;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "skill_matching_analyses")
public class SkillMatchingAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @OneToMany(mappedBy = "skillMatchingAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchedSkill> matchedSkills;

    @OneToMany
    @JoinColumn(name = "unmatched_analysis_id")
    private List<JobSkill> unmatchedJobSkills;

    @ManyToMany
    @JoinTable(
            name = "unmatched_user_skills_map",
            joinColumns = @JoinColumn(name = "analysis_id"),
            inverseJoinColumns = @JoinColumn(name = "user_skill_id")
    )
    private List<UserSkill> unmatchedUserSkills;
}
