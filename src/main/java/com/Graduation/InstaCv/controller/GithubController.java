package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.model.Github.GithubProfile;
import com.Graduation.InstaCv.service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/github/test")
public class GithubController {

    private static final Logger logger = Logger.getLogger(GithubController.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();
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

    @GetMapping("/token-info")
    public ResponseEntity<?> tokenInfo(@RequestParam String accessToken) {
        Map<String, Object> result = new HashMap<>();

        // Check token format
        result.put("tokenLength", accessToken.length());
        result.put("tokenPrefix", accessToken.length() >= 4 ? accessToken.substring(0, 4) : accessToken);

        if (accessToken.startsWith("gho_")) {
            result.put("tokenType", "Fine-grained personal access token");
        } else if (accessToken.startsWith("ghp_")) {
            result.put("tokenType", "Classic personal access token");
        } else if (accessToken.length() == 40 && accessToken.matches("[a-f0-9]+")) {
            result.put("tokenType", "Classic OAuth token");
        } else {
            result.put("tokenType", "Unknown token format");
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/Repo")
    public ResponseEntity<?> getRepoLanguagesReadme(@RequestParam String accessToken) {
        try {
            // Set up headers for GitHub API requests
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + accessToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Create result container
            List<Map<String, Object>> repositories = new ArrayList<>();

            // Get repositories
            ResponseEntity<List> reposResponse = restTemplate.exchange(
                    "https://api.github.com/user/repos",
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            List<Map<String, Object>> repos = reposResponse.getBody();

            // Process each repository
            for (Map<String, Object> repo : repos) {
                Map<String, Object> repoData = new HashMap<>();
                String repoName = (String) repo.get("name");
                String repoOwner = (String) ((Map)repo.get("owner")).get("login");

                // Add repository name
                repoData.put("name", repoName);

                // Get languages for this repository
                try {
                    String languagesUrl = (String) repo.get("languages_url");
                    ResponseEntity<Map> languagesResponse = restTemplate.exchange(
                            languagesUrl,
                            HttpMethod.GET,
                            entity,
                            Map.class
                    );

                    Map<String, Object> languages = languagesResponse.getBody();
                    repoData.put("languages", languages);
                } catch (Exception e) {
                    repoData.put("languages", Collections.emptyMap());
                }

                // Get README content
                try {
                    // Set up headers for raw content
                    HttpHeaders rawHeaders = new HttpHeaders();
                    rawHeaders.set("Authorization", "token " + accessToken);
                    rawHeaders.set("Accept", "application/vnd.github.v3.raw");
                    HttpEntity<String> rawEntity = new HttpEntity<>(rawHeaders);

                    ResponseEntity<String> readmeResponse = restTemplate.exchange(
                            "https://api.github.com/repos/" + repoOwner + "/" + repoName + "/readme",
                            HttpMethod.GET,
                            rawEntity,
                            String.class
                    );

                    repoData.put("readme", readmeResponse.getBody());
                } catch (Exception e) {
                    repoData.put("readme", "");
                }

                repositories.add(repoData);
            }

            return ResponseEntity.ok(repositories);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
