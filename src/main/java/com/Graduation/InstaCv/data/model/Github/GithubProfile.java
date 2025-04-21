package com.Graduation.InstaCv.data.model.Github;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubProfile {
    private String username;
    private String name;
    private String bio;
    private String avatarUrl;
    private List<GithubRepository> repositories;
}