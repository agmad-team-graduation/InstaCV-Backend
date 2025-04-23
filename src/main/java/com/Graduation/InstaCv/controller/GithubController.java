package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.AuthLink;
import com.Graduation.InstaCv.data.model.github.GithubProfile;
import com.Graduation.InstaCv.service.GithubService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github/test")
@AllArgsConstructor
public class GithubController {
    private final GithubService githubService;

    @GetMapping("/authorize")
    public ResponseEntity<AuthLink> authorize() {
        return ResponseEntity.ok(githubService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<GithubAccessTokenResponse> callback(@RequestParam String code) {
        return ResponseEntity.ok(githubService.getAccessToken(code));
    }

    @GetMapping("/profile")
    public ResponseEntity<GithubProfile> profile(@RequestParam String accessToken,
                                                 @RequestParam(defaultValue = "false") boolean forceFetch) {
        return ResponseEntity.ok(githubService.getUserProfile(accessToken, forceFetch));
    }
}
