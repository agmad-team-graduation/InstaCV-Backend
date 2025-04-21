package com.Graduation.InstaCv.gateways.github;

import com.Graduation.InstaCv.data.dto.response.GithubRepoResponse;
import com.Graduation.InstaCv.data.dto.response.GithubUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "githubApiClient", url = "https://api.github.com")
public interface GithubApiClient {
    @GetMapping("/user")
    GithubUserResponse getUser(@RequestHeader("Authorization") String token);

    @GetMapping("/user/repos")
    List<GithubRepoResponse> getRepos(@RequestHeader("Authorization") String token);

    @GetMapping("/repos/{fullName}/languages")
    Map<String, Long> getLanguages(@RequestHeader("Authorization") String token, @PathVariable String fullName);
}

