package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper implements Mapper<Profile, ProfileDto> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ProfileDto mapTo(Profile profile) {
        return ProfileDto.builder()
                .userId(profile.getUser().getId())
                .personalDetails(profile.getPersonalDetails())
                .educationList(profile.getEducationList())
                .experienceList(profile.getExperienceList())
                .userSkills(profile.getUserSkills())
                .projects(profile.getProjects())
                .addedJobs(profile.getAddedJobs())
                .build();
    }

    @Override
    public Profile mapFrom(ProfileDto profileDto) {
        User user = userRepository.findById(profileDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + profileDto.getUserId()));

        return Profile.builder()
                .user(user)
                .personalDetails(profileDto.getPersonalDetails())
                .educationList(profileDto.getEducationList())
                .experienceList(profileDto.getExperienceList())
                .userSkills(profileDto.getUserSkills())
                .projects(profileDto.getProjects())
                .addedJobs(profileDto.getAddedJobs())
                .build();
    }
}