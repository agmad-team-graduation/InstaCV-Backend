package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.data.model.profile.Project;
import com.Graduation.InstaCv.data.model.profile.UserSkill;

public interface IProfileService {
    Profile getProfileByUserId(Long userId);
    Long getProfileIdByUserId(Long userId);
    Profile createProfile(Long userId, Profile profile);
    Profile updateProfile(Long userId, ProfileDto profile);
    Profile addGithubSkillsIntoProfile(Long userId);
    Profile addSkill(Long userId, UserSkill skill);
    Profile addProject(Long userId, Project project);
    void deleteGithubProfile(Long userId);
}
