package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.data.model.JobSkill;
import com.Graduation.InstaCv.data.model.MatchedSkill;
import com.Graduation.InstaCv.data.model.TailoredCv;
import com.Graduation.InstaCv.data.model.profile.*;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.repository.TailoredCvRepository;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.ICvGenerationService;
import jakarta.transaction.Transactional;
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
            job = jobService.analyzeJob(jobId, false).join();
            jobRepository.save(job);
        }

        // Make sure job is matching-analyzed
        if (!job.isMatchingAnalyzed()) {
            job = jobService.AnalyzeJobMatching(jobId, userId);
            jobRepository.save(job);
        }

        // Start building tailored CV
        TailoredCv tailoredCv = TailoredCv.builder()
                .userId(userId)
                .job(job)
                .personalDetails(profile.getPersonalDetails())
                .createdAt(LocalDateTime.now())
                .build();

        // Get skills required by the job
        Set<String> requiredHardSkills = job.getHardSkills().stream()
                .map(JobSkill::getSkill)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<String> requiredSoftSkills = job.getSoftSkills().stream()
                .map(JobSkill::getSkill)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<MatchedSkill> matchedSkills = job.getSkillMatchingAnalysis().getMatchedSkills();
        matchedSkills.sort(Comparator.comparing(MatchedSkill::getSimilarity).reversed());

        List<UserSkill> tailoredSkills = matchedSkills.stream()
                .map(MatchedSkill::getUserSkill)
                .toList();

        tailoredCv.setSkills(tailoredSkills);

        // Sort experiences by date
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
        // TODO: Consider adding a more sophisticated matching algorithm, and use project skills instead of description
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
        String summary = generateProfileSummary(profile, job);
        tailoredCv.setSummary(summary);

        // Calculate match score
        // TODO: Consider using a more sophisticated scoring algorithm
//        int matchScore = calculateMatchScore(profile, job);
//        tailoredCv.setMatchScore(matchScore);

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

//    private int calculateMatchScore(Profile profile, Job job) {
//        // TODO: Remove this and introduce it as another service, that will have details about the score/matching/un-matching skills/recommendations
//        // Count matching skills
//        Set<String> userSkills = profile.getUserSkills().stream()
//                .map(skill -> skill.getSkill().toLowerCase())
//                .collect(Collectors.toSet());
//
//        Set<String> jobSkills = new HashSet<>();
//        if (job.getJobAnalysis().getHardSkills() != null) {
//            jobSkills.addAll(job.getJobAnalysis().getHardSkills().stream()
//                    .map(skill -> skill.getSkill().toLowerCase())
//                    .collect(Collectors.toSet()));
//        }
//        if (job.getJobAnalysis().getSoftSkills() != null) {
//            jobSkills.addAll(job.getJobAnalysis().getSoftSkills().stream()
//                    .map(skill -> skill.getSkill().toLowerCase())
//                    .collect(Collectors.toSet()));
//        }
//
//        long matchingSkills = userSkills.stream()
//                .filter(jobSkills::contains)
//                .count();
//
//        // Score is percentage of job skills matched
//        return jobSkills.isEmpty() ? 0 : (int) (matchingSkills * 100 / jobSkills.size());
//    }
}