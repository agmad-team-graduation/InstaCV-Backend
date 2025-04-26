package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.cv.EducationCv;
import com.Graduation.InstaCv.data.model.cv.ExperienceCv;
import com.Graduation.InstaCv.data.model.cv.ProjectCv;
import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import com.Graduation.InstaCv.data.model.cv.skills.UserSkillCv;
import com.Graduation.InstaCv.data.model.jobMatching.projectMatching.MatchedProject;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.MatchedSkill;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.*;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.JobRepository;
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
        Optional<TailoredCv> existingCv = tailoredCvRepository.findByUserIdAndJobId(userId, jobId);
        if (existingCv.isPresent()) {
            return existingCv.get();
        }

        // Get user and profile
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Profile profile = user.getProfile();
        if (profile == null) {
            throw new ResourceNotFoundException("User has no profile");
        }

        // Get job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // Make sure job is analyzed
        if (!job.isAnalyzed()) {
            job = jobService.analyzeJob(jobId, user.getId(), false).join();
            job = jobRepository.save(job);
        }

        // Make sure job is matching-analyzed
        if (!job.isSkillMatchingAnalyzed()) {
            job = jobService.analyzeJobMatching(jobId, userId);
            job = jobRepository.save(job);
        }

        if (!job.isProjectMatchingAnalyzed()) {
            job = jobService.analyzeProjectsMatching(jobId, userId);
            job = jobRepository.save(job);
        }

        // Start building tailored CV
        TailoredCv tailoredCv = TailoredCv.builder()
                .userId(userId)
                .job(job)
                .personalDetails(profile.getPersonalDetails())
                .createdAt(LocalDateTime.now())
                .build();

        List<MatchedSkill> matchedSkills = job.getSkillMatchingAnalysis().getMatchedSkills();
        matchedSkills.sort(Comparator.comparing(MatchedSkill::getSimilarity).reversed());

        List<UserSkill> tailoredSkills = matchedSkills.stream()
                .map(MatchedSkill::getUserSkill)
                .toList();

        // convert to UserSkillCv
        tailoredCv.setSkills(tailoredSkills.stream().map(userSkillCvMapper::mapFrom).toList());

        // Sort experiences by date
        List<Experience> tailoredExperience = profile.getExperienceList().stream()
                .sorted(Comparator.comparing(Experience::getStartDate).reversed())
                .toList();

        // Convert to ExperienceCv
        tailoredCv.setExperience(tailoredExperience.stream().map(experienceCvMapper::mapFrom).toList());

        // Sort education by date
        List<Education> tailoredEducation = profile.getEducationList().stream()
                .sorted(Comparator.comparing(Education::getStartDate).reversed())
                .toList();

        // Convert to EducationCv
        tailoredCv.setEducation(tailoredEducation.stream().map(educationCvMapper::mapFrom).toList());

        // Include relevant projects
        List<Project> tailoredProjects = job.getProjectMatchingAnalysis().getProjectsMatchedWithSkills()
                .stream().sorted(Comparator.comparing(MatchedProject::getMatchedSkillsCount).reversed())
                .map(MatchedProject::getProject).toList();

        // Convert to ProjectCv
        tailoredCv.setProjects(tailoredProjects.stream().map(projectCvMapper::mapFrom).toList());

        // Generate summary
        String summary = generateProfileSummary(profile, job);
        tailoredCv.setSummary(summary);


        // Save and return
        return tailoredCvRepository.save(tailoredCv);
    }

    @Override
    public TailoredCv getCvById(Long cvId) {
        return tailoredCvRepository.findById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found with id: " + cvId));
    }

    @Override
    public List<TailoredCv> getCvsByUserId(Long userId) {
        return tailoredCvRepository.findByUserId(userId);
    }

    @Override
    public TailoredCv getCvByUserIdAndJobId(Long userId, Long jobId) {
        return tailoredCvRepository.findByUserIdAndJobId(userId, jobId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found for user: " + userId + " and job: " + jobId));
    }

    private long countMatches(String text, Set<String> keywords) {
        if (text == null || keywords == null) return 0;
        String lowerText = text.toLowerCase();
        return keywords.stream()
                .filter(keyword -> lowerText.contains(keyword.toLowerCase()))
                .count();
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
}