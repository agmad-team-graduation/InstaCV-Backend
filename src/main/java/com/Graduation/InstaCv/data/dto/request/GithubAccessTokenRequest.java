package com.Graduation.InstaCv.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GithubAccessTokenRequest {
    private String accessToken;
    private boolean forceRefresh;
}
