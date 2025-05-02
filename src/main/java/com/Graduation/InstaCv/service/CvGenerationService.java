package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.cv.*;
import com.Graduation.InstaCv.data.model.cv.skills.UserSkillCv;
import com.Graduation.InstaCv.data.model.jobMatching.projectMatching.MatchedProject;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.MatchedSkill;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.*;
import com.Graduation.InstaCv.data.dto.TailoredCvDto;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.repository.ProfileRepository;
import com.Graduation.InstaCv.repository.TailoredCvRepository;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.ICvGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CvGenerationService implements ICvGenerationService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JobRepository jobRepository;
    private final TailoredCvRepository tailoredCvRepository;
    private final JobService jobService;
    private final Mapper<UserSkillCv, UserSkill> userSkillCvMapper;
    private final Mapper<ExperienceCv, Experience> experienceCvMapper;
    private final Mapper<EducationCv, Education> educationCvMapper;
    private final Mapper<ProjectCv, Project> projectCvMapper;


    @Override
//    @Transactional
    public TailoredCv generateCv(Long userId, Long jobId) {
        // Check if CV already exists for this user and job
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Profile profile = user.getProfile();
        if (profile == null) throw new ResourceNotFoundException("User has no profile");

        Optional<TailoredCv> existingCv = tailoredCvRepository.findByIdAndProfileId(profile.getId(), jobId);
        if (existingCv.isPresent()) {
            return existingCv.get();
        }

        // Get job
        Job job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // Make sure job is analyzed
        if (!job.isAnalyzed()) {
            job = jobService.analyzeJob(jobId, user.getId(), false).join();
            job = jobRepository.save(job);
        }

        // Make sure job is matching-analyzed
        if (!job.isSkillMatchingAnalyzed()) {
            job = jobService.analyzeJobMatching(jobId, userId, false);
            job = jobRepository.save(job);
        }

        if (!job.isProjectMatchingAnalyzed()) {
            job = jobService.analyzeProjectsMatching(jobId, userId);
            job = jobRepository.save(job);
        }

        // Start building tailored CV
        TailoredCv tailoredCv = TailoredCv.builder()
                .profile(profile)
                .job(job)
                .personalDetails(profile.getPersonalDetails())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<MatchedSkill> matchedSkills = job.getSkillMatchingAnalysis().getMatchedSkills();
        matchedSkills.sort(Comparator.comparing(MatchedSkill::getSimilarity).reversed());

        List<UserSkill> tailoredSkills = matchedSkills.stream()
                .map(MatchedSkill::getUserSkill)
                .sorted(Comparator.comparing(UserSkill::getLevel).reversed())
                .toList();

        // convert to UserSkillCv
        tailoredCv.setSkillSection(
                SkillSection.builder().items(
                        tailoredSkills.stream().map(userSkillCvMapper::mapFrom).toList()
                ).build());

        // Sort experiences by date
        List<Experience> tailoredExperience = profile.getExperienceList().stream()
                .sorted(Comparator.comparing(Experience::getStartDate).reversed())
                .toList();

        // Convert to ExperienceCv
        tailoredCv.setExperienceSection(
                ExperienceSection.builder().items(
                        tailoredExperience.stream().map(experienceCvMapper::mapFrom).toList()
                ).build());

        // Sort education by date
        List<Education> tailoredEducation = profile.getEducationList().stream()
                .sorted(Comparator.comparing(Education::getStartDate).reversed())
                .toList();

        // Convert to EducationCv
        tailoredCv.setEducationSection(
                EducationSection.builder().items(
                        tailoredEducation.stream().map(educationCvMapper::mapFrom).toList()
                ).build());

        // Include relevant projects
        List<Project> tailoredProjects = job.getProjectMatchingAnalysis().getProjectsMatchedWithSkills()
                .stream().sorted(Comparator.comparing(MatchedProject::getMatchedSkillsCount).reversed())
                .map(MatchedProject::getProject).toList();

        // Convert to ProjectCv
        tailoredCv.setProjectSection(
                ProjectSection.builder().items(
                        tailoredProjects.stream().map(projectCvMapper::mapFrom).toList()
                ).build());

        // Generate summary
        String summary = generateProfileSummary(profile, job);
        tailoredCv.setSummary(summary);


        // Set relationships
        tailoredCv.getEducationSection().getItems().forEach(x -> x.setSection(tailoredCv.getEducationSection()));
        tailoredCv.getExperienceSection().getItems().forEach(x -> x.setSection(tailoredCv.getExperienceSection()));
        tailoredCv.getProjectSection().getItems().forEach(x -> x.setSection(tailoredCv.getProjectSection()));
        tailoredCv.getSkillSection().getItems().forEach(x -> x.setSection(tailoredCv.getSkillSection()));

        // Set order index
        setEducationOrderIndex(tailoredCv.getEducationSection().getItems());
        setExperienceOrderIndex(tailoredCv.getExperienceSection().getItems());
        setProjectOrderIndex(tailoredCv.getProjectSection().getItems());
        setSkillOrderIndex(tailoredCv.getSkillSection().getItems());

        // Set order index for sections
        tailoredCv.getEducationSection().setOrderIndex(0);
        tailoredCv.getExperienceSection().setOrderIndex(1);
        tailoredCv.getProjectSection().setOrderIndex(2);
        tailoredCv.getSkillSection().setOrderIndex(3);

        // Save and return
        return tailoredCvRepository.save(tailoredCv);
    }

    private void setEducationOrderIndex(List<EducationCv> items) {
        for (int i = 0; i < items.size(); i++)
            items.get(i).setOrderIndex(i);
    }

    private void setExperienceOrderIndex(List<ExperienceCv> items) {
        for (int i = 0; i < items.size(); i++)
            items.get(i).setOrderIndex(i);
    }

    private void setProjectOrderIndex(List<ProjectCv> items) {
        for (int i = 0; i < items.size(); i++)
            items.get(i).setOrderIndex(i);
    }

    private void setSkillOrderIndex(List<UserSkillCv> items) {
        for (int i = 0; i < items.size(); i++)
            items.get(i).setOrderIndex(i);
    }

    @Override
    public TailoredCv getCvByIdAndUserId(Long cvId, Long userId) {
        Long profileId = profileRepository.findProfileIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        return tailoredCvRepository.findByIdAndProfileId(cvId, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found or not accessible for user: " + userId));
    }

    @Override
    public List<TailoredCv> getCvsByUserId(Long userId) {
        Long profileId = profileRepository.findProfileIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        return tailoredCvRepository.findByProfileId(profileId);
    }

    @Override
    public TailoredCv getCvByJobIdAndUserId(Long jobId, Long userId) {
        Long profileId = profileRepository.findProfileIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        boolean jobExists = jobRepository.existsByIdAndProfileId(jobId, profileId);
        if (!jobExists)
            throw new ResourceNotFoundException("Job not found for user: " + userId + " and job: " + jobId);
        return tailoredCvRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found for user: " + userId + " and job: " + jobId));
    }

    private String generateProfileSummary(Profile profile, Job job) {
        // Simple summary generation logic
        String jobTitle = job.getTitle();
        String company = job.getCompany();

        return "Professional with experience in " +
                profile.getUserSkills().stream()
                        .limit(5)
                        .map(UserSkill::getSkill)
                        .collect(Collectors.joining(", ")) +
                " seeking a position as " + jobTitle + " at " + company + ".";
    }

    @Override
    public TailoredCv updateCv(Long cvId, Long userId, TailoredCvDto tailoredCvDto) {
        // Get the CV and validate ownership
        TailoredCv existingCv = getCvByIdAndUserId(cvId, userId);

        // Update basic fields
        if (tailoredCvDto.getPersonalDetails() != null) {
            existingCv.setPersonalDetails(tailoredCvDto.getPersonalDetails());
        }
        if (tailoredCvDto.getSummary() != null) {
            existingCv.setSummary(tailoredCvDto.getSummary());
        }

        // Update education section
        if (tailoredCvDto.getEducationSection() != null) {
            existingCv.setEducationSection(tailoredCvDto.getEducationSection());
            // Set relationships and order index
            existingCv.getEducationSection().getItems().forEach(item -> {
                item.setSection(existingCv.getEducationSection());
            });
            setEducationOrderIndex(existingCv.getEducationSection().getItems());
        }

        // Update experience section
        if (tailoredCvDto.getExperienceSection() != null) {
            existingCv.setExperienceSection(tailoredCvDto.getExperienceSection());
            // Set relationships and order index
            existingCv.getExperienceSection().getItems().forEach(item -> {
                item.setSection(existingCv.getExperienceSection());
            });
            setExperienceOrderIndex(existingCv.getExperienceSection().getItems());
        }

        // Update skill section
        if (tailoredCvDto.getSkillSection() != null) {
            existingCv.setSkillSection(tailoredCvDto.getSkillSection());
            // Set relationships and order index
            existingCv.getSkillSection().getItems().forEach(item -> {
                item.setSection(existingCv.getSkillSection());
            });
            setSkillOrderIndex(existingCv.getSkillSection().getItems());
        }

        // Update project section
        if (tailoredCvDto.getProjectSection() != null) {
            existingCv.setProjectSection(tailoredCvDto.getProjectSection());
            // Set relationships and order index
            existingCv.getProjectSection().getItems().forEach(item -> {
                item.setSection(existingCv.getProjectSection());
                // Handle project skills
                if (item.getSkills() != null) {
                    item.getSkills().forEach(skill -> {
                        skill.setProjectCv(item);
                    });
                }
            });
            setProjectOrderIndex(existingCv.getProjectSection().getItems());
        }

        // Set updated timestamp
        existingCv.setUpdatedAt(LocalDateTime.now());

        // Save and return
        return tailoredCvRepository.save(existingCv);
    }
}