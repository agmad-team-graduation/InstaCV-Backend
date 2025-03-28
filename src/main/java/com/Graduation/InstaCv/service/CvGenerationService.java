package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.data.model.JobSkill;
import com.Graduation.InstaCv.data.model.TailoredCv;
import com.Graduation.InstaCv.data.model.profile.*;
import com.Graduation.InstaCv.exceptions.JobNotFoundException;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
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

    @Override
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
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        // Make sure job is analyzed
        if (!job.isAnalyzed()) {
            job = jobService.analyzeJob(jobId, false).join();
        }

        // Start building tailored CV
        TailoredCv tailoredCv = TailoredCv.builder()
                .userId(userId)
                .job(job)
                .personalDetails(profile.getPersonalDetails())
                .createdAt(LocalDateTime.now())
                .build();

        // Get skills required by the job
        Set<String> requiredHardSkills = job.getJobAnalysis().getHardSkills().stream()
                .map(JobSkill::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        Set<String> requiredSoftSkills = job.getJobAnalysis().getSoftSkills().stream()
                .map(JobSkill::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // Prioritize skills that match job requirements and by skill level
        List<Skill> tailoredSkills = profile.getSkills().stream()
                .sorted((s1, s2) -> {
                    // First, check job requirement match
                    boolean s1Match = requiredHardSkills.contains(s1.getSkill().toLowerCase()) 
                            || requiredSoftSkills.contains(s1.getSkill().toLowerCase());
                    boolean s2Match = requiredHardSkills.contains(s2.getSkill().toLowerCase())
                            || requiredSoftSkills.contains(s2.getSkill().toLowerCase());
                    
                    // If match status is different, prioritize matches
                    if (s1Match && !s2Match) return -1;
                    if (!s1Match && s2Match) return 1;
                    
                    // If both match or both don't match, sort by skill level
                    if (s1.getLevel() != null && s2.getLevel() != null) {
                        // Compare ordinals in reverse (higher skill level should be first)
                        return Integer.compare(s2.getLevel().ordinal(), s1.getLevel().ordinal());
                    } else if (s1.getLevel() != null) {
                        return -1; // s1 has level, s2 doesn't, so s1 comes first
                    } else if (s2.getLevel() != null) {
                        return 1;  // s2 has level, s1 doesn't, so s2 comes first
                    }
                    
                    // If neither has a level, maintain original order
                    return 0;
                })
                .collect(Collectors.toList());
        
        tailoredCv.setSkills(tailoredSkills);

        // Sort experiences by relevance to job
        List<Experience> tailoredExperience = profile.getExperienceList().stream()
                .sorted(Comparator.comparing(Experience::getStartDate).reversed())
                .collect(Collectors.toList());
        
        tailoredCv.setExperience(tailoredExperience);

        // Sort education by date
        List<Education> tailoredEducation = profile.getEducationList().stream()
                .sorted(Comparator.comparing(Education::getStartDate).reversed())
                .collect(Collectors.toList());
        
        tailoredCv.setEducation(tailoredEducation);

        // Include relevant projects
        List<Project> tailoredProjects = profile.getProjects().stream()
                .sorted((p1, p2) -> {
                    // Count skill matches in project description
                    long p1Matches = countMatches(p1.getDescription(), requiredHardSkills);
                    long p2Matches = countMatches(p2.getDescription(), requiredHardSkills);
                    return Long.compare(p2Matches, p1Matches);
                })
                .collect(Collectors.toList());
        
        tailoredCv.setProjects(tailoredProjects);

        // Generate summary
        String summary = generateSummary(profile, job);
        tailoredCv.setSummary(summary);

        // Calculate match score
        int matchScore = calculateMatchScore(profile, job);
        tailoredCv.setMatchScore(matchScore);

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

    private String generateSummary(Profile profile, Job job) {
        // Simple summary generation logic
        String jobTitle = job.getTitle();
        String company = job.getCompany();
        
        return "Professional with experience in " + 
                profile.getSkills().stream()
                        .limit(5)
                        .map(Skill::getSkill)
                        .collect(Collectors.joining(", ")) +
                " seeking a position as " + jobTitle + " at " + company + ".";
    }

    private int calculateMatchScore(Profile profile, Job job) {
        // Count matching skills
        Set<String> userSkills = profile.getSkills().stream()
                .map(skill -> skill.getSkill().toLowerCase())
                .collect(Collectors.toSet());
        
        Set<String> jobSkills = new HashSet<>();
        if (job.getJobAnalysis().getHardSkills() != null) {
            jobSkills.addAll(job.getJobAnalysis().getHardSkills().stream()
                    .map(skill -> skill.getName().toLowerCase())
                    .collect(Collectors.toSet()));
        }
        if (job.getJobAnalysis().getSoftSkills() != null) {
            jobSkills.addAll(job.getJobAnalysis().getSoftSkills().stream()
                    .map(skill -> skill.getName().toLowerCase())
                    .collect(Collectors.toSet()));
        }
        
        long matchingSkills = userSkills.stream()
                .filter(jobSkills::contains)
                .count();
        
        // Score is percentage of job skills matched
        return jobSkills.isEmpty() ? 0 : (int) (matchingSkills * 100 / jobSkills.size());
    }
} 