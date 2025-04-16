package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProfileService implements IProfileService {
    private UserRepository userRepository;

    @Override
    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public User fullUpdateProfile(Long userId, Profile profile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        user.setProfile(profile);
        user.setProfileCreated(true);
        profile.setUser(user);

        profile.getEducationList().forEach(education -> education.setProfile(profile));
        profile.getExperienceList().forEach(experience -> experience.setProfile(profile));
        profile.getProjects().forEach(project -> project.setProfile(profile));
        profile.getUserSkills().forEach(userSkill -> userSkill.setProfile(profile));
        profile.getAddedJobs().forEach(job -> job.setProfile(profile));

        profile.getProjects().forEach(project -> project.getSkills().forEach(projectSkill -> projectSkill.setProject(project)));

        return userRepository.save(user);
    }
}
