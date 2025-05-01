package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.github.RepoSkill;
import com.Graduation.InstaCv.data.model.profile.*;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.ProfileRepository;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                if (project.getSkills() != null) {
                    project.getSkills().forEach(projectSkill -> {
                        projectSkill.setId(null);
                        projectSkill.setProject(project);
                    });
                }
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

    @Override
    public Profile addGithubSkillsIntoProfile(Long userId) {
        // Fetch the user's profile
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user ID: " + userId));

        // Ensure the user has connected their GitHub profile
        if (profile.getGithubProfile() == null || profile.getGithubProfile().getUsername() == null)
            throw new IllegalStateException("GitHub profile is not connected for user ID: " + userId);

        List<RepoSkill> githubSkills = profile.getGithubProfile().getSkills();
        if (githubSkills == null)
            throw new IllegalStateException("Error getting GitHub skills for user ID: " + userId + ", try re-fetching the GitHub profile.");

        // Map GitHub skills to UserSkill entities, avoiding duplicates
        Set<String> existingSkillNames = profile.getUserSkills().stream()
                .map(UserSkill::getSkill)
                .collect(Collectors.toSet());

        // Filter out existing skills of github profile skills
        List<UserSkill> newSkills = githubSkills.stream()
                .filter(githubSkill -> !existingSkillNames.contains(githubSkill.getSkill()))
                .map(githubSkill -> UserSkill.builder().skill(githubSkill.getSkill()).profile(profile).build())
                .collect(Collectors.toList());

        // Add the new skills to the profile
        profile.getUserSkills().addAll(newSkills);

        // Save the updated profile
        return profileRepository.save(profile);
    }
}
