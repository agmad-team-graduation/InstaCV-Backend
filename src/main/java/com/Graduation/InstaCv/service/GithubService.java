package com.Graduation.InstaCv.service;


import com.Graduation.InstaCv.data.dto.request.GithubAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.GithubRepoResponse;
import com.Graduation.InstaCv.data.dto.response.GithubUserResponse;
import com.Graduation.InstaCv.data.dto.request.AccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.GithubAuthLink;
import com.Graduation.InstaCv.data.model.UserPhoto;
import com.Graduation.InstaCv.data.model.github.GithubProfile;
import com.Graduation.InstaCv.data.model.github.GithubRepository;
import com.Graduation.InstaCv.data.model.github.RepoSkill;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.exceptions.FetchErrorException;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.GithubSkillRepository;
import com.Graduation.InstaCv.repository.ProfileRepository;
import com.Graduation.InstaCv.service.Interfaces.IGithubService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GithubService implements IGithubService {
    private final WebClient.Builder webClientBuilder;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.callback.url}")
    private String callbackUrl;
    @Value("${github.login-callback.url}")
    private String loginCallbackUrl;

    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);
    private final GithubSkillRepository githubSkillRepository;
    private final ProfileRepository profileRepository;

    private final UserService userService;

    public GithubService(WebClient.Builder webClientBuilder, GithubSkillRepository githubSkillRepository, ProfileRepository profileRepository, UserService userService) {
        this.webClientBuilder = webClientBuilder;
        this.githubSkillRepository = githubSkillRepository;
        this.profileRepository = profileRepository;
        this.userService = userService;
    }

    public GithubAuthLink getAuthorizationUrl(boolean isLogin) {
        return GithubAuthLink.builder()
                .authLink("https://github.com/login/oauth/authorize?client_id=" + clientId +
                        "&scope=user,repo&redirect_uri="
                        + (isLogin ? loginCallbackUrl : callbackUrl)
                )
                .build();
    }

    @Override
    public GithubUserResponse getUserProfileInfo(GithubAccessTokenResponse tokenResponse) {
        if (tokenResponse == null || StringUtil.isNullOrEmpty(tokenResponse.getAccessToken())) {
            throw new IllegalArgumentException("Access token is required to fetch GitHub profile info");
        }

        String accessToken = tokenResponse.getAccessToken();

        try {
            WebClient webClient = webClientBuilder.build();

            // Fetch user profile
            GithubUserResponse userResponse = webClient.get()
                    .uri("https://api.github.com/user")
                    .header("Authorization", "token " + accessToken)
                    .retrieve()
                    .bodyToMono(GithubUserResponse.class)
                    .block();

            if (userResponse.getLogin() != null && profileRepository.existsByGithubProfileUsername(userResponse.getLogin())) {
                throw new FetchErrorException("GitHub profile with username " + userResponse.getLogin() + " already exists");
            }

            // If email is null, fetch from /user/emails
            if (userResponse != null && (userResponse.getEmail() == null || userResponse.getEmail().isEmpty())) {
                List<Map<String, Object>> emails = webClient.get()
                        .uri("https://api.github.com/user/emails")
                        .header("Authorization", "token " + accessToken)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                        })
                        .block();
                if (emails != null) {
                    // Find the primary, verified email
                    String primaryEmail = emails.stream()
                            .filter(e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
                            .map(e -> (String) e.get("email"))
                            .findFirst()
                            .orElse(null);
                    if (primaryEmail == null) {
                        // fallback: any verified email
                        primaryEmail = emails.stream()
                                .filter(e -> Boolean.TRUE.equals(e.get("verified")))
                                .map(e -> (String) e.get("email"))
                                .findFirst()
                                .orElse(null);
                    }
                    userResponse.setEmail(primaryEmail);
                }
            }
            return userResponse;
        } catch (WebClientResponseException e) {
            logger.error("Error fetching user profile info: {}", e.getMessage());
            throw new FetchErrorException("Failed to fetch GitHub profile info", e);
        }
    }

    public GithubAccessTokenResponse getAccessToken(String code) {
        AccessTokenRequest requestDto = AccessTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .build();

        try {
            WebClient webClient = webClientBuilder.build();

            return webClient.post()
                    .uri("https://github.com/login/oauth/access_token")
                    .header("Accept", "application/json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(GithubAccessTokenResponse.class)
                    .block();
        } catch (Exception e) {
            logger.error("Error exchanging code for access token: {}", e.getMessage());
            throw new FetchErrorException("Failed to exchange code for access token", e);
        }
    }

    public GithubProfile getUserProfile(GithubAccessTokenRequest request) {
        boolean forceRefresh = request.isForceRefresh();
        try {
            Long userId = SecurityUtils.getCurrentUserDetails().getId();
            Profile profile = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id " + userId));

            GithubProfile oldGithubProfile = profile.getGithubProfile();

            if (!forceRefresh && oldGithubProfile != null)
                // Make a copy to avoid lazy loading issues
                return GithubProfile.builder()
                        .username(oldGithubProfile.getUsername())
                        .name(oldGithubProfile.getName())
                        .bio(oldGithubProfile.getBio())
                        .avatarUrl(oldGithubProfile.getAvatarUrl())
                        .repositories(oldGithubProfile.getRepositories())
                        .skills(oldGithubProfile.getSkills())
                        .build();

            if (request == null || StringUtil.isNullOrEmpty(request.getAccessToken())) {
                throw new IllegalArgumentException("Access token is required to fetch GitHub profile");
            }

            String accessToken = request.getAccessToken();
            // Use WebClient instead of Feign client
            String tokenHeader = "token " + accessToken;

            WebClient webClient = webClientBuilder.build();
            GithubUserResponse userDetails = webClient.get()
                    .uri("https://api.github.com/user")
                    .header("Authorization", tokenHeader)
                    .retrieve()
                    .bodyToMono(GithubUserResponse.class)
                    .block();

            if (userDetails.getLogin() != null && profileRepository.existsByGithubProfileUsername(userDetails.getLogin())) {
                throw new FetchErrorException("GitHub profile with username " + userDetails.getLogin() + " already exists");
            }

            if (!userService.hasPhoto()) {
                // Create a new UserPhoto with GitHub's avatar URL
                UserPhoto githubPhoto = UserPhoto.builder()
                        .user(userService.getCurrentUser())
                        .photoUrl(userDetails.getAvatar_url())
                        .photoFormat("url")  // Since it's a direct URL, not a file
                        .uploadedAt(new Date())
                        .build();
                userService.saveUserPhoto(githubPhoto);
            }

            // If accessToken is valid, we can fetch the user details and remove the old profile, else it throws error
            if (oldGithubProfile != null) {
                profile.setGithubProfile(null);
                profileRepository.save(profile);
            }
            List<GithubRepoResponse> repos = getAllRepositories(tokenHeader);
            GithubProfile githubProfile = GithubProfile.builder()
                    .username(userDetails.getLogin())
                    .name(userDetails.getName())
                    .bio(userDetails.getBio())
                    .avatarUrl(userDetails.getAvatar_url())
                    .build();

            Set<RepoSkill> profileSetSkills = new HashSet<>();
            Set<RepoSkill> allGithubSkills = new HashSet<>(githubSkillRepository.findAll());

            List<GithubRepository> reposWithLanguagesAndReadme = repos.stream()
                    .map(repo -> {
                        List<RepoSkill> languages = getLanguagesFromClient(tokenHeader, repo.getFullName())
                                .stream()
                                .map(skill -> getOrCreateRepoSkill(skill, allGithubSkills))
                                .toList();
                        profileSetSkills.addAll(languages);
                        String readmeContent = getReadmeFromClient(tokenHeader, repo.getFullName());
                        return GithubRepository.builder()
                                .name(repo.getName())
                                .description(repo.getDescription())
                                .languages(languages)
                                .readmeContent(readmeContent)
                                .htmlUrl(repo.getHtmlUrl())
                                .build();
                    }).collect(Collectors.toList());

            reposWithLanguagesAndReadme.forEach(repo -> repo.setGithubProfile(githubProfile));
            githubProfile.setRepositories(reposWithLanguagesAndReadme);
            githubProfile.setSkills(profileSetSkills.stream().toList());
            profile.setGithubProfile(githubProfile);

            return profileRepository.save(profile).getGithubProfile();
        } catch (WebClientResponseException e) {
            logger.error("Error fetching user profile: {}", e.getMessage());
            throw new FetchErrorException("Failed to fetch GitHub profile", e);
        }
    }

    private RepoSkill getOrCreateRepoSkill(String skill, Set<RepoSkill> allGithubSkills) {
        return allGithubSkills.stream()
                .filter(existingSkill -> existingSkill.getSkill().equals(skill))
                .findFirst()
                .orElseGet(() -> {
                    RepoSkill newSkill = RepoSkill.builder().skill(skill).build();
                    allGithubSkills.add(newSkill);
                    return githubSkillRepository.save(newSkill);
                });
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
            WebClient webClient = webClientBuilder.build();

            Map<String, Long> languages = webClient.get()
                    .uri("https://api.github.com/repos/" + fullName + "/languages")
                    .header("Authorization", tokenHeader)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {
                    })
                    .block();

            return new ArrayList<>(languages.keySet());
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Languages not found for repository: {}", fullName);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching languages for repository: {}", fullName, e);
            throw new FetchErrorException("Failed to fetch languages", e);
        }
    }

    private List<GithubRepoResponse> getAllRepositories(String tokenHeader) {
        List<GithubRepoResponse> allRepos = new ArrayList<>();
        final int perPage = 100; // Maximum allowed by GitHub API
        int currentPage = 1;

        WebClient webClient = webClientBuilder.build();

        while (true) {
            try {
                String uri = String.format("https://api.github.com/user/repos?page=%d&per_page=%d&visibility=public",
                        currentPage, perPage);

                List<GithubRepoResponse> reposPage = webClient.get()
                        .uri(uri)
                        .header("Authorization", tokenHeader)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<GithubRepoResponse>>() {
                        })
                        .block();

                if (reposPage == null || reposPage.isEmpty()) break;
                allRepos.addAll(reposPage);
                if (reposPage.size() < perPage) break;
                currentPage++;
            } catch (Exception e) {
                logger.error("Error fetching repositories page {}: {}", currentPage, e.getMessage());
                throw new FetchErrorException("Failed to fetch GitHub repositories", e);
            }
        }
        return allRepos;
    }
}
