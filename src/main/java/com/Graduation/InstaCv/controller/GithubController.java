package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.request.GithubAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.GithubAuthLink;
import com.Graduation.InstaCv.data.model.github.GithubProfile;
import com.Graduation.InstaCv.service.GithubService;
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
    @Value("${frontend.url}")
    private String frontendUrl;

    @GetMapping("/authorize")
    public ResponseEntity<GithubAuthLink> authorize() {
        return ResponseEntity.ok(githubService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code, HttpServletResponse response) throws IOException {
        GithubAccessTokenResponse tokenResponse = githubService.getAccessToken(code);

        Cookie cookie = new Cookie("github_token", tokenResponse.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        response.sendRedirect(frontendUrl);
    }


    @PostMapping("/profile")
    public ResponseEntity<GithubProfile> profile(@CookieValue(value = "github_token", required = false) String githubToken) {
        if (githubToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(githubService.getUserProfile(new GithubAccessTokenRequest(githubToken)));
    }
}
