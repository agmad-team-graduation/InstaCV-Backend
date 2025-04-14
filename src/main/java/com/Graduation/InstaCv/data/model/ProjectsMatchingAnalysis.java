package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.model.profile.Project;
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
@Table(name = "project_matching_analyses")
public class ProjectsMatchingAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @OneToMany(mappedBy = "projectsMatchingAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchedProject> matchedProjects;

    @ManyToMany
    @JoinTable(
            name = "unmatched_projects_map",
            joinColumns = @JoinColumn(name = "analysis_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> unMatchedProjects;
}

