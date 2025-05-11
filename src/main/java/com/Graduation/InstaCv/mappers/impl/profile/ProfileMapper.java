package com.Graduation.InstaCv.mappers.impl.profile;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProfileMapper implements ContextAwareMapper<Profile, ProfileDto, User> {
    @Override
    public ProfileDto mapTo(Profile profile) {
        if (profile == null)
            return null;
        return ProfileDto.builder()
                .profileId(profile.getId())
                .personalDetails(profile.getPersonalDetails())
                .educationList(profile.getEducationList())
                .experienceList(profile.getExperienceList())
                .userSkills(profile.getUserSkills())
                .projects(profile.getProjects())
                .build();
    }

    @Override
    public Profile mapFrom(ProfileDto profileDto, User user) {
        if (profileDto == null)
            return null;

        return Profile.builder()
                .user(user)
                .personalDetails(profileDto.getPersonalDetails())
                .educationList(profileDto.getEducationList())
                .experienceList(profileDto.getExperienceList())
                .userSkills(profileDto.getUserSkills())
                .projects(profileDto.getProjects())
                .build();
    }
}