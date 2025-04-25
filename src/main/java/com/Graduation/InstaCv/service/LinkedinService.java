package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.request.LinkedinAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.AuthLink;
import com.Graduation.InstaCv.data.dto.response.LinkedinAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.linkedin.LinkedinEducationResponse;
import com.Graduation.InstaCv.data.dto.response.linkedin.LinkedinExperienceResponse;
import com.Graduation.InstaCv.data.dto.response.linkedin.LinkedinProfileResponse;
import com.Graduation.InstaCv.data.dto.response.linkedin.LinkedinSkillResponse;
import com.Graduation.InstaCv.data.model.linkedin.LinkedinEducation;
import com.Graduation.InstaCv.data.model.linkedin.LinkedinExperience;
import com.Graduation.InstaCv.data.model.linkedin.LinkedinProfile;
import com.Graduation.InstaCv.data.model.linkedin.LinkedinSkill;
import com.Graduation.InstaCv.gateways.linkedin.LinkedinApiClient;
import com.Graduation.InstaCv.gateways.linkedin.LinkedinAuthClient;
import com.Graduation.InstaCv.repository.LinkedinProfileRepository;
import com.Graduation.InstaCv.service.Interfaces.ILinkedinService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LinkedinService implements ILinkedinService {

    @Value("${linkedin.client.id}")
    private String clientId;

    @Value("${linkedin.client.secret}")
    private String clientSecret;

    @Value("${linkedin.callback.url}")
    private String callbackUrl;

    private final LinkedinProfileRepository profileRepository;

    private final LinkedinAuthClient authClient;
    private final LinkedinApiClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(LinkedinService.class);

    public LinkedinService(LinkedinProfileRepository profileRepository, LinkedinAuthClient authClient, LinkedinApiClient apiClient) {
        this.profileRepository = profileRepository;
        this.authClient = authClient;
        this.apiClient = apiClient;
    }

    @Override
    public AuthLink getAuthorizationUrl() {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://www.linkedin.com/oauth/v2/authorization")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", callbackUrl)
                .queryParam("scope", "profile email")
                .encode()           // percent-encode reserved characters (spaces -> %20)
                .toUriString();
        return AuthLink.builder().authLink(url).build();
    }

    public LinkedinAccessTokenResponse getAccessToken(String code) {
        Map<String, String> form = new HashMap<>();
        form.put("grant_type", "authorization_code");
        form.put("code", code);
        form.put("redirect_uri", callbackUrl);
        form.put("client_id", clientId);
        form.put("client_secret", clientSecret);

        return authClient.getAccessToken(form);
    }

    @Override
    public LinkedinProfile getUserProfile(String accessToken, boolean forceFetch) {
        try {
            String bearer = "Bearer " + accessToken;
            LinkedinProfileResponse profileResp = apiClient.getProfile(bearer);

            if (!forceFetch) {
                Optional<LinkedinProfile> cached = profileRepository.findById(profileResp.getId());
                if (cached.isPresent()) return cached.get();
            }

            // Fetch skills, education, experience
            List<LinkedinSkillResponse> skills = safeCall(() -> apiClient.getSkills(bearer));
            List<LinkedinEducationResponse> edus = safeCall(() -> apiClient.getEducations(bearer));
            List<LinkedinExperienceResponse> exps = safeCall(() -> apiClient.getExperiences(bearer));

            // Build entity
            LinkedinProfile profile = LinkedinProfile.builder()
                    .id(profileResp.getId())
                    .firstName(profileResp.getFirstName())
                    .lastName(profileResp.getLastName())
                    .headline(profileResp.getHeadline())
                    .profilePictureUrl(profileResp.getProfilePictureUrl())
                    .build();

            // Map and attach skills
            List<LinkedinSkill> skillEntities = skills.stream()
                    .map(s -> LinkedinSkill.builder().name(s.getName()).profile(profile).build())
                    .collect(Collectors.toList());
            profile.setSkills(skillEntities);

            // Map education
            List<LinkedinEducation> eduEntities = edus.stream()
                    .map(e -> LinkedinEducation.builder()
                            .schoolName(e.getSchoolName())
                            .degreeName(e.getDegreeName())
                            .startDate(e.getStartDate())
                            .endDate(e.getEndDate())
                            .profile(profile)
                            .build())
                    .collect(Collectors.toList());
            profile.setEducations(eduEntities);

            // Map experience
            List<LinkedinExperience> expEntities = exps.stream()
                    .map(x -> LinkedinExperience.builder()
                            .companyName(x.getCompanyName())
                            .title(x.getTitle())
                            .startDate(x.getStartDate())
                            .endDate(x.getEndDate())
                            .profile(profile)
                            .build())
                    .collect(Collectors.toList());
            profile.setExperiences(expEntities);

            return profileRepository.save(profile);
        } catch (Exception e) {
            logger.error("Error fetching LinkedIn profile", e);
            throw new RuntimeException("Failed to fetch LinkedIn profile", e);
        }
    }

    private <T> T safeCall(FeignSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (FeignException.NotFound nf) {
            logger.warn("LinkedIn resource not found: {}", nf.status());
            return (T) Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching LinkedIn resource", e);
            return (T) Collections.emptyList();
        }
    }

    @FunctionalInterface
    private interface FeignSupplier<R> {
        R get();
    }
}
