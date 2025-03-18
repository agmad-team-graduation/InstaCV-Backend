package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper implements Mapper<Profile, ProfileDto> {
    @Override
    public ProfileDto mapTo(Profile profile) {
        return ProfileDto.builder()
                .personalDetails(profile.getPersonalDetails())
                .educationList(profile.getEducationList())
                .experienceList(profile.getExperienceList())
                .skills(profile.getSkills())
                .projects(profile.getProjects())
                .build();
    }

    @Override
    public Profile mapFrom(ProfileDto profileDto) {
        return Profile.builder()
                .personalDetails(profileDto.getPersonalDetails())
                .educationList(profileDto.getEducationList())
                .experienceList(profileDto.getExperienceList())
                .skills(profileDto.getSkills())
                .projects(profileDto.getProjects())
                .build();
    }
}
