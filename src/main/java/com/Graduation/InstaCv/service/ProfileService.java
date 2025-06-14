package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.github.RepoSkill;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.*;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.JobRepository;
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
    private JobRepository jobRepository;
    private UserRepository userRepository;

    @Override
    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id " + userId));
    }

    @Override
    public Long getProfileIdByUserId(Long userId) {
        return profileRepository.findProfileIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile ID not found for user with id " + userId));
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

        if (updatedProfile.getUserSkills() != null) {
            List<Job> profileJobs = jobRepository.findJobsByProfileId(existingProfile.getId());
            for (Job job : profileJobs) {
                job.getSkillMatchingAnalyses().clear();
            }
            jobRepository.saveAll(profileJobs);
            // TODO: Remove all matching for external jobs also, Test it
            List<Job> analyzedScrapedJobsByProfileId = jobRepository.findAnalyzedScrapedJobsByProfileId(existingProfile.getId());
            for (Job job : analyzedScrapedJobsByProfileId) {
                job.getSkillMatchingAnalyses().remove(
                        job.getSkillMatchingAnalyses().stream()
                                .filter(analysis -> analysis.getProfile().getId().equals(existingProfile.getId()))
                                .findFirst()
                                .orElse(null)
                );
            }
            jobRepository.saveAll(analyzedScrapedJobsByProfileId);
        }

        if (updatedProfile.getProjects() != null) {
            // remove project skills usages in projectAnalysis, and any job
            List<Job> profileJobs = jobRepository.findJobsByProfileId(existingProfile.getId());
            for (Job job : profileJobs) {
                job.getProjectMatchingAnalyses().clear();
            }
            jobRepository.saveAll(profileJobs);
            // TODO: Remove all matching for external jobs also, Test it
            List<Job> analyzedScrapedJobsByProfileId = jobRepository.findAnalyzedScrapedJobsByProfileId(existingProfile.getId());
            for (Job job : analyzedScrapedJobsByProfileId) {
                job.getProjectMatchingAnalyses().remove(
                        job.getProjectMatchingAnalyses().stream()
                                .filter(analysis -> analysis.getProfile().getId().equals(existingProfile.getId()))
                                .findFirst()
                                .orElse(null)
                );
            }
            jobRepository.saveAll(analyzedScrapedJobsByProfileId);
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

    @Override
    public Profile addSkill(Long userId, UserSkill skill) {
        // Fetch the user's profile
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id " + userId));

        // Check if the skill already exists in the profile
        boolean skillExists = profile.getUserSkills().stream()
                .anyMatch(existingSkill -> existingSkill.getSkill().equalsIgnoreCase(skill.getSkill()));

        if (skillExists) {
            throw new IllegalStateException("Skill already exists in the profile");
        }

        // Set the profile for the new skill
        skill.setProfile(profile);
        skill.setId(null); // Ensure the ID is null for a new entry

        // Add the new skill to the profile
        profile.getUserSkills().add(skill);

        // Save the updated profile
        return profileRepository.save(profile);
    }

    @Override
    public Profile addProject(Long userId, Project project) {
        // Fetch the user's profile
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id " + userId));

        // Set the profile for the new project
        project.setProfile(profile);
        project.setId(null); // Ensure the ID is null for a new entry

        // Initialize skills if they are null
        if (project.getSkills() == null) {
            project.setSkills(List.of());
        }

        project.getSkills().forEach(skill -> {
            skill.setProject(project);
            skill.setId(null); // Ensure the ID is null for a new entry
        });

        // Add the new project to the profile
        profile.getProjects().add(project);

        // Save the updated profile
        return profileRepository.save(profile);
    }
}
