package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.request.GithubAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.GithubAuthLink;
import com.Graduation.InstaCv.data.model.github.GithubProfile;
import com.Graduation.InstaCv.service.GithubService;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/github/test")
@RequiredArgsConstructor
public class GithubController {
    private final GithubService githubService;
    private final IProfileService profileService;

    @GetMapping("/authorize")
    public ResponseEntity<GithubAuthLink> authorize() {
        return ResponseEntity.ok(githubService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<GithubAccessTokenResponse> callback(@RequestParam String code) {
        GithubAccessTokenResponse tokenResponse = githubService.getAccessToken(code);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/profile")
    public ResponseEntity<GithubProfile> profile(@RequestBody GithubAccessTokenRequest request) {
        return ResponseEntity.ok(githubService.getUserProfile(request));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        profileService.deleteGithubProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
