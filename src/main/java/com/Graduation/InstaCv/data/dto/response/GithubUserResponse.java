package com.Graduation.InstaCv.data.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GithubUserResponse {
    private String login;
    private String name;
    private String bio;
    private String avatar_url;
}