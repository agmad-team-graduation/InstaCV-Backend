package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.dto.UserDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/profiles/")
public class ProfileController {
    private final IProfileService profileService;
    private final Mapper<Profile, ProfileDto> profileMapper;
    private final Mapper<User, UserDto> userMapper;

    @GetMapping("/{userId}")
    public UserDto getProfile(@PathVariable UUID userId) {
        User user = profileService.getProfile(userId);
        return userMapper.mapTo(user);
    }

    @PostMapping("/create/{userId}")
    public UserDto createProfile(@PathVariable UUID userId, @RequestBody ProfileDto profileDto) {
        Profile profile = profileMapper.mapFrom(profileDto);
        User updatedUser = profileService.fullUpdateProfile(userId, profile);
        return userMapper.mapTo(updatedUser);
    }

    @PostMapping("/full-update/{userId}")
    public UserDto fullUpdateProfile(@PathVariable UUID userId, ProfileDto profile) {
        return createProfile(userId, profile);
    }
}
