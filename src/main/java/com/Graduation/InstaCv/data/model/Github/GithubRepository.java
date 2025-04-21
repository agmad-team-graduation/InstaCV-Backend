package com.Graduation.InstaCv.data.model.Github;

import java.util.List;
import java.util.Map;

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
