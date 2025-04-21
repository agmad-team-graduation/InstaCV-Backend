package com.Graduation.InstaCv.data.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepoDto {
    private String name;
    private String description;
    private String languages_url;
}
