package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.GithubAuthLink;
import com.Graduation.InstaCv.data.model.github.GithubProfile;

public interface IGithubService {
    GithubProfile getUserProfile(String accessToken, boolean forceFetch);

    GithubAccessTokenResponse getAccessToken(String code);

    GithubAuthLink getAuthorizationUrl();
}
