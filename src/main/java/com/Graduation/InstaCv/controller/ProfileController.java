package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import com.Graduation.InstaCv.service.AiCvParserService;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/profiles/")
public class ProfileController {
    private final IProfileService profileService;
    private final ContextAwareMapper<Profile, ProfileDto, User> profileMapper;
    private final AiCvParserService aiCvParserService;

    @GetMapping("/me")
    public ProfileDto getProfile() {
        // Extract userId from the security context
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Profile profile = profileService.getProfileByUserId(userId);
        return profileMapper.mapTo(profile);
    }

    @PostMapping("/create")
    public ProfileDto createProfile(@RequestBody ProfileDto profileDto) {
        // Extract userId from the security context
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Profile createdProfile = profileService.createProfile(userId, profileMapper.mapFrom(profileDto, null));
        return profileMapper.mapTo(createdProfile);
    }

    @PutMapping("/update")
    public ProfileDto updateProfile(@RequestBody ProfileDto profileDto) {
        // Extract userId from the security context
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Profile updatedProfile = profileService.updateProfile(userId, profileDto);
        return profileMapper.mapTo(updatedProfile);
    }

    @PutMapping("/add-github-skills")
    public ProfileDto addGithubSkillsIntoProfile() {
        // Extract userId from the security context
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Profile updatedProfile = profileService.addGithubSkillsIntoProfile(userId);
        return profileMapper.mapTo(updatedProfile);
    }

    @PostMapping("/upload-cv")
    public ResponseEntity<ProfileDto> uploadCvAndCreateProfile(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = SecurityUtils.getCurrentUserDetails().getId();
            // Parse CV using AI
            ProfileDto parsedProfile = aiCvParserService.parseCV(file);
            // Update existing profile
            Profile updatedProfile = profileService.updateProfile(userId, parsedProfile);
            return ResponseEntity.ok(profileMapper.mapTo(updatedProfile));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process CV");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().<ProfileDto>build();
        }
    }

}
