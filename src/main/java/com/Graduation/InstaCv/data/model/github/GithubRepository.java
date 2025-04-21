package com.Graduation.InstaCv.data.model.github;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubRepository {
    private String name;
    private String description;
    private List<String> languages;
    private String readmeContent;
}
