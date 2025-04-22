package com.Graduation.InstaCv.data.model.github;

import java.util.List;

import com.Graduation.InstaCv.data.model.profile.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
}