package com.Graduation.InstaCv.data.model.github;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "github_profiles")
public class GithubProfile {
    @Id
    private String username;
    private String name;
    private String bio;
    private String avatarUrl;
    @OneToMany(mappedBy = "githubProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GithubRepository> repositories;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "github_profile_skills_mapping",
            joinColumns = @JoinColumn(name = "github_profile_username"),
            inverseJoinColumns = @JoinColumn(name = "repo_skill_id")
    )
    private List<RepoSkill> skills;
}