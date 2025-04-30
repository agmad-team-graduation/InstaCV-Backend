package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.*;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.ProfileRepository;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProfileService implements IProfileService {
    private ProfileRepository profileRepository;
    private UserRepository userRepository;

    @Override
    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id " + userId));
    }

    @Override
    public Profile createProfile(Long userId, Profile newProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        Profile oldProfile = user.getProfile();
        if (oldProfile != null)
            throw new IllegalStateException("User already has a profile");

        user.setProfile(newProfile);
        user.setProfileCreated(true);

        newProfile.setId(null);
        newProfile.setGithubProfile(null);
        newProfile.setUser(user);

        newProfile.getEducationList().forEach(education -> {
            education.setProfile(newProfile);
            education.setId(null);
        });
        newProfile.getExperienceList().forEach(experience -> {
            experience.setProfile(newProfile);
            experience.setId(null);
        });
        newProfile.getProjects().forEach(project -> {
            project.setProfile(newProfile);
            project.setId(null);
            project.getSkills().forEach(projectSkill -> {
                projectSkill.setProject(project);
                projectSkill.setId(null);
            });
        });
        newProfile.getUserSkills().forEach(userSkill -> {
            userSkill.setProfile(newProfile);
            userSkill.setId(null);
        });

        return profileRepository.save(newProfile);
    }

    @Override
    public Profile updateProfile(Long userId, ProfileDto updatedProfile) {
        Profile existingProfile = getProfileByUserId(userId);
        if (existingProfile == null)
            throw new ResourceNotFoundException("Profile not found for user to update with id " + userId);

        if (updatedProfile.getPersonalDetails() != null)
            existingProfile.setPersonalDetails(updatedProfile.getPersonalDetails());

        if (updatedProfile.getEducationList() != null) {
            List<Education> existingEducationList = existingProfile.getEducationList();
            existingEducationList.clear();
            updatedProfile.getEducationList().forEach(education -> {
                education.setId(null);
                education.setProfile(existingProfile);
                existingEducationList.add(education);
            });
        }
        if (updatedProfile.getExperienceList() != null) {
            List<Experience> existingExperienceList = existingProfile.getExperienceList();
            existingExperienceList.clear();
            updatedProfile.getExperienceList().forEach(experience -> {
                experience.setId(null);
                experience.setProfile(existingProfile);
                existingExperienceList.add(experience);
            });
        }
        if (updatedProfile.getProjects() != null) {
            List<Project> existingProjects = existingProfile.getProjects();
            existingProjects.clear();
            updatedProfile.getProjects().forEach(project -> {
                project.setId(null);
                project.setProfile(existingProfile);
                project.getSkills().forEach(projectSkill -> {
                    projectSkill.setId(null);
                    projectSkill.setProject(project);
                });
                existingProjects.add(project);
            });
        }
        if (updatedProfile.getUserSkills() != null) {
            List<UserSkill> existingUserSkills = existingProfile.getUserSkills();
            existingUserSkills.clear();
            updatedProfile.getUserSkills().forEach(userSkill -> {
                userSkill.setId(null);
                userSkill.setProfile(existingProfile);
                existingUserSkills.add(userSkill);
            });
        }
        return profileRepository.save(existingProfile);
    }
}
