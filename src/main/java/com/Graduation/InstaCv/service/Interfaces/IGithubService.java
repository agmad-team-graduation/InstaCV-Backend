package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.request.GithubAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.GithubAuthLink;
import com.Graduation.InstaCv.data.model.github.GithubProfile;

public interface IGithubService {
    GithubProfile getUserProfile(GithubAccessTokenRequest request, boolean forceRefresh);

    GithubAccessTokenResponse getAccessToken(String code);

    GithubAuthLink getAuthorizationUrl();
}
