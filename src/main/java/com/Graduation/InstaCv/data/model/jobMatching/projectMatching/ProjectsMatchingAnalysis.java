package com.Graduation.InstaCv.data.model.jobMatching.projectMatching;

import com.Graduation.InstaCv.data.model.job.Job;
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
@Table(name = "project_matching_analyses")
public class ProjectsMatchingAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Job job;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore
    private Profile profile;

    @OneToMany(mappedBy = "projectsMatchingAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchedProject> allAnalyzedProjects;

    public List<MatchedProject> getProjectsMatchedWithSkills() {
        return allAnalyzedProjects.stream()
                .filter(matchedProject -> matchedProject.getMatchedSkillsCount() > 0)
                .toList();
    }

    public List<MatchedProject> getProjectMatchedWithNoSkills() {
        return allAnalyzedProjects.stream()
                .filter(matchedProject -> matchedProject.getMatchedSkillsCount() == 0)
                .toList();
    }
}

