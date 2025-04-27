package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.ProfileRepository;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

        if (user.getProfile() != null)
            throw new IllegalStateException("User already has a profile");

        user.setProfile(newProfile);
        user.setProfileCreated(true);
        newProfile.setUser(user);

        newProfile.getEducationList().forEach(education -> education.setProfile(newProfile));
        newProfile.getExperienceList().forEach(experience -> experience.setProfile(newProfile));
        newProfile.getProjects().forEach(project -> project.setProfile(newProfile));
        newProfile.getUserSkills().forEach(userSkill -> userSkill.setProfile(newProfile));
        newProfile.getAddedJobs().forEach(job -> job.setProfile(newProfile));

        newProfile.getProjects().forEach(project -> project.getSkills().forEach(projectSkill -> projectSkill.setProject(project)));
        return profileRepository.save(newProfile);
    }
}
