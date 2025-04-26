package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.dto.UserDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.security.UserDetailsImpl;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import com.sun.net.httpserver.HttpContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/profiles/")
public class ProfileController {
    private final IProfileService profileService;
    private final Mapper<Profile, ProfileDto> profileMapper;

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
        Profile createdProfile = profileService.fullUpdateProfile(userId, profileMapper.mapFrom(profileDto));
        return profileMapper.mapTo(createdProfile);
    }

}
