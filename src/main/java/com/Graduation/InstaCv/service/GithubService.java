package com.Graduation.InstaCv.service;


import com.Graduation.InstaCv.data.dto.GithubRepoDto;
import com.Graduation.InstaCv.data.dto.GithubUserDto;
import com.Graduation.InstaCv.data.dto.request.AccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.AccessTokenResponse;
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
import java.util.stream.Collectors;

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
        AccessTokenRequest requestDto = new AccessTokenRequest(clientId, clientSecret, code);

        return webClientBuilder.build()
                .post()
                .uri("https://github.com/login/oauth/access_token")
                .header("Accept", "application/json")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .map(AccessTokenResponse::getAccess_token)
                .block();
    }

    public GithubProfile getUserProfile(String accessToken) {
        try {
            // Fetch user details
            GithubUserDto userDetails = webClientBuilder.build()
                    .get()
                    .uri("https://api.github.com/user")
                    .header("Authorization", "token " + accessToken)
                    .retrieve()
                    .bodyToMono(GithubUserDto.class)
                    .block();

            if (userDetails == null) {
                throw new RuntimeException("Failed to retrieve user details from GitHub");
            }

            // Fetch repositories
            List<GithubRepoDto> repos = webClientBuilder.build()
                    .get()
                    .uri("https://api.github.com/user/repos")
                    .header("Authorization", "token " + accessToken)
                    .retrieve()
                    .bodyToFlux(GithubRepoDto.class)
                    .collectList()
                    .block();

            if (repos == null) {
                throw new RuntimeException("Failed to retrieve repositories from GitHub");
            }

            // Convert DTOs to domain objects
            GithubProfile profile = GithubProfile.builder().
                    username(userDetails.getLogin())
                    .name(userDetails.getName())
                    .bio(userDetails.getBio())
                    .avatarUrl(userDetails.getAvatar_url())
                    .build();

            List<GithubRepository> repositories = repos.stream()
                    .map(repo -> {
                        // Fetch languages and extract keys directly
                        List<String> languageList = new ArrayList<>(
                                Objects.requireNonNull(
                                        webClientBuilder.build()
                                                .get()
                                                .uri(repo.getLanguages_url())
                                                .header("Authorization", "token " + accessToken)
                                                .retrieve()
                                                .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {
                                                })
                                                .block()
                                ).keySet()
                        );

                        // Fetch README content
                        String readmeContent = getReadmeContent(accessToken, userDetails.getLogin(), repo.getName());

                        // Create repository object
                        return GithubRepository.builder()
                                .name(repo.getName())
                                .description(repo.getDescription())
                                .languages(languageList)
                                .readmeContent(readmeContent)
                                .build();
                    })
                    .collect(Collectors.toList());

            profile.setRepositories(repositories);
            return profile;

        } catch (WebClientResponseException e) {
            logger.error("Error fetching data from GitHub API: Status code: {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("GitHub API error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching GitHub profile", e);
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage(), e);
        }
    }

    private String getReadmeContent(String accessToken, String owner, String repo) {
        try {
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
                return "";
            }
            logger.error("Error fetching README for repo: {}/{}. Status code: {}, Response body: {}",
                    owner, repo, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return "";
        } catch (Exception e) {
            logger.error("Error fetching README for repo: {}/{}", owner, repo, e);
            return "";
        }
    }
}