package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.response.ExtractedJobSkillResponse;
import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.repository.ProfileRepository;
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class JobService implements IJobService {
    private final JobRepository jobRepository;
    private final ProfileRepository profileRepository;
    private final JobSkillService jobSkillService;
    private final Mapper<JobSkill, ExtractedJobSkillResponse> jobSkillMapper;

    @Override
    public Job addJob(Long userId, Job job) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        job.setProfile(profile);
        return jobRepository.save(job);
    }

    @Override
    @Async
    public CompletableFuture<Job> analyzeJob(Long jobId, Long userId, boolean forceAnalyze) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        return jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .map(job -> analyzeIfNeeded(job, forceAnalyze))
                .orElseThrow(() -> new ResourceNotFoundException("Job with ID " + jobId + " not found"));
    }

    @Override
    public Job analyzeJobMatching(Long jobId, Long userId, boolean forceAnalyze) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        Job job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        if (!forceAnalyze && job.isSkillMatchingAnalyzed()) return job;
        job = analyzeIfNeeded(job, false).join();
        job.setSkillMatchingAnalysis(jobSkillService.analyzeSkillsMatching(job, profile.getUser()));
        job.setSkillMatchingAnalyzed(true);
        return jobRepository.save(job);
    }

    @Override
    public Job analyzeProjectsMatching(Long jobId, Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        Job job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        job = analyzeIfNeeded(job, false).join();
        job.setProjectMatchingAnalysis(jobSkillService.analyzeProjectsMatching(job, profile.getUser()));
        job.getProjectMatchingAnalysis().setJob(job);
        job.setProjectMatchingAnalyzed(true);
        return jobRepository.save(job);
    }

    @Override
    public Job getJobByIdAndUserId(Long jobId, Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        return jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId + " for user with id: " + userId));
    }

    @Override
    public void deleteJobByIdAndUserId(Long jobId, Long userId) {
        Job job = getJobByIdAndUserId(jobId, userId); // ensures ownership
        jobRepository.delete(job);
    }

    @Override
    public List<Job> getJobsByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + userId));
        return jobRepository.findJobsByProfileId(profile.getId());
    }

    private CompletableFuture<Job> analyzeIfNeeded(Job job, boolean forceAnalyze) {
        if (job.isAnalyzed() && !forceAnalyze) return CompletableFuture.completedFuture(job);

        CompletableFuture<JobKnowledgeResponse> knowledgePredictions = jobSkillService.extractKnowledge(job.getDescription());
        CompletableFuture<JobSkillsResponse> skillsPredictions = jobSkillService.extractSkills(job.getDescription());

        return CompletableFuture.allOf(knowledgePredictions, skillsPredictions)
                .thenApply(v -> updateJobWithAnalysis(job, knowledgePredictions.join(), skillsPredictions.join()))
                .thenApply(jobRepository::save);
    }

    private Job updateJobWithAnalysis(Job job, JobKnowledgeResponse knowledge, JobSkillsResponse skills) {
        List<JobSkill> hardSkills = knowledge.getKnowledgePredictions().stream()
                .map(jobSkillMapper::mapFrom)
                .toList();
        hardSkills.forEach(jobSkill -> jobSkill.setSkillType(SkillType.HARD));

        List<JobSkill> softSkills = skills.getSkillsPredictions().stream()
                .map(jobSkillMapper::mapFrom)
                .toList();
        softSkills.forEach(jobSkill -> jobSkill.setSkillType(SkillType.SOFT));

        // Clear and add new skills, instead of directly setting to avoid orphan removal error
        job.getJobSkills().clear();
        job.getJobSkills().addAll(hardSkills);
        job.getJobSkills().addAll(softSkills);

        job.setAnalyzed(true);

        job.getJobSkills().forEach(jobSkill -> jobSkill.setJob(job));
        return job;
    }
}