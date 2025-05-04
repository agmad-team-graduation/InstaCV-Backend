package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.cv.EducationCv;
import com.Graduation.InstaCv.data.model.cv.ExperienceCv;
import com.Graduation.InstaCv.data.model.cv.ProjectCv;
import com.Graduation.InstaCv.data.model.cv.TailoredCv;
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

        Job job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.isAnalyzeFailed()) // re-analyze the job if it failed
            job = jobService.fullAnalyze(jobId, userId, false);
        else if (!job.isAnalyzed() || !job.isSkillMatchingAnalyzed() || !job.isProjectMatchingAnalyzed()) // job is being analyzed
            throw new ResourceNotFoundException("Job not analyzed yet with id: " + jobId);

        // Start building tailored CV
        TailoredCv tailoredCv = TailoredCv.builder()
                .profile(profile)
                .job(job)
                .personalDetails(profile.getPersonalDetails())
                .createdAt(LocalDateTime.now())
                .build();

        List<MatchedSkill> matchedSkills = job.getSkillMatchingAnalysis().getMatchedSkills();
        matchedSkills.sort(Comparator.comparing(MatchedSkill::getSimilarity).

                reversed());

        List<UserSkill> tailoredSkills = matchedSkills.stream()
                .map(MatchedSkill::getUserSkill)
                .toList();

        // convert to UserSkillCv
        tailoredCv.setSkills(tailoredSkills.stream().

                map(userSkillCvMapper::mapFrom).

                toList());

        // Sort experiences by date
        List<Experience> tailoredExperience = profile.getExperienceList().stream()
                .sorted(Comparator.comparing(Experience::getStartDate).reversed())
                .toList();

        // Convert to ExperienceCv
        tailoredCv.setExperience(tailoredExperience.stream().

                map(experienceCvMapper::mapFrom).

                toList());

        // Sort education by date
        List<Education> tailoredEducation = profile.getEducationList().stream()
                .sorted(Comparator.comparing(Education::getStartDate).reversed())
                .toList();

        // Convert to EducationCv
        tailoredCv.setEducation(tailoredEducation.stream().

                map(educationCvMapper::mapFrom).

                toList());

        // Include relevant projects
        List<Project> tailoredProjects = job.getProjectMatchingAnalysis().getProjectsMatchedWithSkills()
                .stream().sorted(Comparator.comparing(MatchedProject::getMatchedSkillsCount).reversed())
                .map(MatchedProject::getProject).toList();

        // Convert to ProjectCv
        tailoredCv.setProjects(tailoredProjects.stream().

                map(projectCvMapper::mapFrom).

                toList());

        // Generate summary
        String summary = generateProfileSummary(profile, job);
        tailoredCv.setSummary(summary);


        // Set relationships
        tailoredCv.getEducation().

                forEach(x -> x.setCv(tailoredCv));
        tailoredCv.getExperience().

                forEach(x -> x.setCv(tailoredCv));
        tailoredCv.getProjects().

                forEach(x -> x.setCv(tailoredCv));
        tailoredCv.getSkills().

                forEach(x -> x.setCv(tailoredCv));


        // Save and return
        return tailoredCvRepository.save(tailoredCv);
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

        // Update education
        if (tailoredCvDto.getEducation() != null) {
            // Clear existing education
            existingCv.getEducation().clear();
            // Add new education
            tailoredCvDto.getEducation().forEach(education -> {
                education.setId(null); // Reset ID to allow new entity creation
                education.setCv(existingCv);
                existingCv.getEducation().add(education);
            });
        }

        // Update experience
        if (tailoredCvDto.getExperience() != null) {
            // Clear existing experience
            existingCv.getExperience().clear();
            // Add new experience
            tailoredCvDto.getExperience().forEach(experience -> {
                experience.setId(null); // Reset ID to allow new entity creation
                experience.setCv(existingCv);
                existingCv.getExperience().add(experience);
            });
        }

        // Update skills
        if (tailoredCvDto.getSkills() != null) {
            // Clear existing skills
            existingCv.getSkills().clear();
            // Add new skills
            tailoredCvDto.getSkills().forEach(skill -> {
                skill.setId(null); // Reset ID to allow new entity creation
                skill.setCv(existingCv);
                existingCv.getSkills().add(skill);
            });
        }

        // Update projects
        if (tailoredCvDto.getProjects() != null) {
            // Clear existing projects
            existingCv.getProjects().clear();
            // Add new projects
            tailoredCvDto.getProjects().forEach(project -> {
                project.setId(null); // Reset ID to allow new entity creation
                project.setCv(existingCv);
                // Handle project skills
                if (project.getSkills() != null) {
                    project.getSkills().forEach(skill -> {
                        skill.setId(null); // Reset ID to allow new entity creation
                        skill.setProjectCv(project);
                    });
                }
                existingCv.getProjects().add(project);
            });
        }

        // Set updated timestamp
        existingCv.setUpdatedAt(LocalDateTime.now());

        // Save and return
        return tailoredCvRepository.save(existingCv);
    }
}