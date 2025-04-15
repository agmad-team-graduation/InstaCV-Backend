package com.Graduation.InstaCv.data.model.Github;

import java.util.List;
import lombok.Data;

@Data
public class GithubProfile {
    private String username;
    private String name;
    private String bio;
    private String avatarUrl;
    private List<GithubRepository> repositories;
}