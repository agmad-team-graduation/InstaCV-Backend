package com.Graduation.InstaCv.gateways.github;

import com.Graduation.InstaCv.data.dto.request.GithubAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "githubAuthClient", url = "https://github.com")
public interface GithubAuthClient {
    @PostMapping(
            value = "/login/oauth/access_token",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json"
    )
    GithubAccessTokenResponse getAccessToken(@RequestBody GithubAccessTokenRequest request);
}