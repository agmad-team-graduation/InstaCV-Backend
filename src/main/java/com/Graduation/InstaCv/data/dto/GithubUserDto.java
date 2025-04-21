package com.Graduation.InstaCv.data.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GithubUserDto {
    private String login;
    private String name;
    private String bio;
    private String avatar_url;
}