package com.Graduation.InstaCv.data.model.jobMatching.projectMatching;

import com.Graduation.InstaCv.data.model.profile.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "matched_projects")
public class MatchedProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "analysis_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProjectsMatchingAnalysis projectsMatchingAnalysis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "matchedProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchedProjectSkill> matchedSkills;
    private Long matchedSkillsCount;
}
