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
                .userSkills(profile.getUserSkills())
                .projects(profile.getProjects())
                .build();
    }

    @Override
    public Profile mapFrom(ProfileDto profileDto) {
        return Profile.builder()
                .personalDetails(profileDto.getPersonalDetails())
                .educationList(profileDto.getEducationList())
                .experienceList(profileDto.getExperienceList())
                .userSkills(profileDto.getUserSkills())
                .projects(profileDto.getProjects())
                .build();
    }
}
