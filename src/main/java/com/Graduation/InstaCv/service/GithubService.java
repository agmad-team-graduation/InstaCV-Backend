package com.Graduation.InstaCv.service;


import com.Graduation.InstaCv.data.model.Github.GithubProfile;
import com.Graduation.InstaCv.data.model.Github.GithubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@Service
public class GithubService {

    private final WebClient.Builder webClientBuilder;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);


    public GithubService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String getAuthorizationUrl() {
        return "https://github.com/login/oauth/authorize?client_id=" + clientId +
                "&scope=user,repo&redirect_uri=http://localhost:8080/api/github/test/callback";
    }

    public String getAccessToken(String code) {
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("code", code);

        return webClientBuilder.build()
                .post()
                .uri("https://github.com/login/oauth/access_token")
                .header("Accept", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .block();
    }
    public GithubProfile getUserProfile(String accessToken) {
        GithubProfile profile = new GithubProfile();

        try {
            // Fetch user details
            Map<String, Object> userDetails = webClientBuilder.build()
                    .get()
                    .uri("https://api.github.com/user")
                    .header("Authorization", "token " + accessToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (userDetails == null) {
                throw new RuntimeException("Failed to retrieve user details from GitHub");
            }

            profile.setUsername((String) userDetails.get("login"));
            profile.setName((String) userDetails.get("name"));
            profile.setBio((String) userDetails.get("bio"));
            profile.setAvatarUrl((String) userDetails.get("avatar_url"));

            // Fetch repositories
            List<Map<String, Object>> repos = webClientBuilder.build()
                    .get()
                    .uri("https://api.github.com/user/repos")
                    .header("Authorization", "token " + accessToken)
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .collectList()
                    .block();

            if (repos == null) {
                throw new RuntimeException("Failed to retrieve repositories from GitHub");
            }

            List<GithubRepository> repositories = new ArrayList<>();

            for (Map<String, Object> repo : repos) {
                GithubRepository repository = new GithubRepository();
                repository.setName((String) repo.get("name"));
                repository.setDescription((String) repo.get("description"));

                // Fetch languages
                String languagesUrl = (String) repo.get("languages_url");
                Map<String, Long> languages = webClientBuilder.build()
                        .get()
                        .uri(languagesUrl)
                        .header("Authorization", "token " + accessToken)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {})
                        .block();

                repository.setLanguages(languages);

                // Fetch README content if it exists
                String readmeContent = getReadmeContent(accessToken, (String) userDetails.get("login"), (String) repo.get("name"));
                repository.setReadmeContent(readmeContent);

                repositories.add(repository);
            }

            profile.setRepositories(repositories);
        } catch (WebClientResponseException e) {
            logger.error("Error fetching data from GitHub API: Status code: {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("GitHub API error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching GitHub profile", e);
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage(), e);
        }

        return profile;
    }

    private String getReadmeContent(String accessToken, String owner, String repo) {
        try {
            // First try to get metadata to check if README exists
            Map<String, Object> readmeMetadata = webClientBuilder.build()
                    .get()
                    .uri("https://api.github.com/repos/" + owner + "/" + repo + "/readme")
                    .header("Authorization", "token " + accessToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (readmeMetadata == null) {
                return "";
            }

            // Now fetch the raw content
            return webClientBuilder.build()
                    .get()
                    .uri("https://api.github.com/repos/" + owner + "/" + repo + "/readme")
                    .header("Authorization", "token " + accessToken)
                    .header("Accept", "application/vnd.github.v3.raw")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                logger.warn("README not found for repo: {}/{}. Status code: {}",
                        owner, repo, e.getStatusCode());
                return ""; // README not found or cannot be accessed
            }
            logger.error("Error fetching README for repo: {}/{}. Status code: {}, Response body: {}",
                    owner, repo, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return ""; // Other error
        } catch (Exception e) {
            logger.error("Error fetching README for repo: {}/{}", owner, repo, e);
            return ""; // General error
        }
    }
}