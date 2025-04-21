package com.Graduation.InstaCv.service;


import com.Graduation.InstaCv.data.dto.response.GithubRepoResponse;
import com.Graduation.InstaCv.data.dto.response.GithubUserResponse;
import com.Graduation.InstaCv.data.dto.request.AccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.GithubAuthLink;
import com.Graduation.InstaCv.data.model.BaseSkill;
import com.Graduation.InstaCv.data.model.github.GithubProfile;
import com.Graduation.InstaCv.data.model.github.GithubRepository;
import com.Graduation.InstaCv.data.model.github.RepoSkill;
import com.Graduation.InstaCv.exceptions.FetchErrorException;
import com.Graduation.InstaCv.gateways.github.GithubApiClient;
import com.Graduation.InstaCv.gateways.github.GithubAuthClient;
import com.Graduation.InstaCv.repository.GithubProfileRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GithubService {

    private final GithubAuthClient githubAuthClient;
    private final GithubApiClient githubApiClient;
    private final WebClient.Builder webClientBuilder;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.callback.url}")
    private String callbackUrl;

    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);

    private final GithubProfileRepository githubProfileRepository;

    public GithubService(GithubAuthClient githubAuthClient, GithubApiClient githubApiClient, WebClient.Builder webClientBuilder, GithubProfileRepository githubProfileRepository) {
        this.githubAuthClient = githubAuthClient;
        this.githubApiClient = githubApiClient;
        this.webClientBuilder = webClientBuilder;
        this.githubProfileRepository = githubProfileRepository;
    }

    public GithubAuthLink getAuthorizationUrl() {
        return GithubAuthLink.builder()
                .authLink("https://github.com/login/oauth/authorize?client_id=" + clientId +
                        "&scope=user,repo&redirect_uri=" + callbackUrl)
                .build();
    }

    public GithubAccessTokenResponse getAccessToken(String code) {
        AccessTokenRequest requestDto = AccessTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .build();
        return githubAuthClient.getAccessToken(requestDto);
    }

    public GithubProfile getUserProfile(String accessToken, boolean forceFetch) {
        try {
            String tokenHeader = "token " + accessToken;
            GithubUserResponse userDetails = githubApiClient.getUser(tokenHeader);

            if (!forceFetch){
                Optional<GithubProfile> tryGetProfile = githubProfileRepository.findByUsername(userDetails.getLogin());
                if (tryGetProfile.isPresent()) {
                    return tryGetProfile.get();
                }
            }

            List<GithubRepoResponse> repos = githubApiClient.getRepos(tokenHeader);

            GithubProfile githubProfile = GithubProfile.builder()
                    .username(userDetails.getLogin())
                    .name(userDetails.getName())
                    .bio(userDetails.getBio())
                    .avatarUrl(userDetails.getAvatar_url())
                    .build();

            List<GithubRepository> reposWithLanguagesAndReadme = repos.stream()
                    .map(repo -> {
                        List<RepoSkill> languages = getLanguagesFromClient(tokenHeader, repo.getFullName())
                                .stream()
                                .map(lang -> BaseSkill.builder().skill(lang).build().asRepoSkill())
                                .toList();
                        String readmeContent = getReadmeFromClient(tokenHeader, repo.getFullName());
                        return GithubRepository.builder()
                                .name(repo.getName())
                                .description(repo.getDescription())
                                .languages(languages)
                                .readmeContent(readmeContent)
                                .build();
                    }).collect(Collectors.toList());

            reposWithLanguagesAndReadme.forEach(repo -> {
                repo.setGithubProfile(githubProfile);
                repo.getLanguages().forEach(lang -> lang.setGithubRepository(repo));
            });
            githubProfile.setRepositories(reposWithLanguagesAndReadme);

            return githubProfileRepository.save(githubProfile);
        } catch (Exception e) {
            logger.error("Error fetching GitHub profile", e);
            throw new FetchErrorException("Failed to fetch GitHub profile", e);
        }
    }

    private String getReadmeFromClient(String tokenHeader, String fullName) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("https://api.github.com/repos/" + fullName + "/readme")
                    .header("Authorization", tokenHeader)
                    .header("Accept", "application/vnd.github.v3.raw")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("README not found for repo: {}", fullName);
            return "";
        } catch (Exception e) {
            logger.error("Error fetching README for repo: {}, error: {}", fullName, e.getMessage());
            return "";
        }
    }

    private List<String> getLanguagesFromClient(String tokenHeader, String fullName) {
        try {
            return new ArrayList<>(githubApiClient.getLanguages(tokenHeader, fullName).keySet());
        } catch (FeignException.FeignClientException.NotFound e) {
            logger.warn("Languages not found for repository: {}", fullName);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching languages for repository: {}", fullName, e);
            throw new FetchErrorException("Failed to fetch languages", e);
        }
    }
}
