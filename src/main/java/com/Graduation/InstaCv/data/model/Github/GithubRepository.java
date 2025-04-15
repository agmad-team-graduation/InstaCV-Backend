package com.Graduation.InstaCv.data.model.Github;

import java.util.Map;
import lombok.Data;


@Data
public class GithubRepository {
    private String name;
    private String description;
    private Map<String, Long> languages;
    private String readmeContent;
}
