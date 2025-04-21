package com.Graduation.InstaCv.data.model.github;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "github_repos")
public class GithubRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "github_profile_username", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private GithubProfile githubProfile;
    // TODO Later: This is better to be a ManyToMany relationship, having all GithubSkills in a table
    @OneToMany(mappedBy = "githubRepository", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepoSkill> languages;
    @Column(length = 100000)
    private String readmeContent;
}
