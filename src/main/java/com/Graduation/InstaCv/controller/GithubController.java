package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.model.Github.GithubProfile;
import com.Graduation.InstaCv.service.GithubService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/github/test")
public class GithubController {

    private static final Logger logger = Logger.getLogger(GithubController.class.getName());
    private final GithubService githubService;

    @Autowired
    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> authorize() {
        String authUrl = githubService.getAuthorizationUrl();
        return ResponseEntity.ok(authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> callback(@RequestParam String code) {
        String accessToken = githubService.getAccessToken(code);

        Map<String, String> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("message", "GitHub authorization successful");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestParam String accessToken) {
        try {
            GithubProfile profile = githubService.getUserProfile(accessToken);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            logger.severe("Error in test profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
