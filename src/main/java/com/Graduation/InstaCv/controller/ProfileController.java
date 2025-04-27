package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/profiles/")
public class ProfileController {
    private final IProfileService profileService;
    private final ContextAwareMapper<Profile, ProfileDto, User> profileMapper;

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

}
