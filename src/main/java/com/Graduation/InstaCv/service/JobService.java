package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.RemoteOkJobDto;
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
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class JobService implements IJobService {
    private final JobRepository jobRepository;
    private final ProfileService profileService;
    private final JobSkillService jobSkillService;
    private final Mapper<JobSkill, ExtractedJobSkillResponse> jobSkillMapper;

    @Override
    public Job addJob(Job job, Profile profile) {
        job.setId(null);
        job.setProfile(profile);
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    @Async
    public void backgroundFullAnalyzeJob(Long jobId, Long userId, Boolean forceAnalyze) {
        try {
            // No need to call skillExtraction (will be done internally)
            Job job = analyzeSkillsMatching(jobId, userId, forceAnalyze);
            jobRepository.save(job);
            job = analyzeProjectsMatching(jobId, userId, forceAnalyze);
            jobRepository.save(job);
        } catch (Exception e) {
            Job job = getJobByIdAndUserId(jobId, userId);
            job.setAnalyzeFailed(true);
            jobRepository.save(job);
        }
    }

    public Job extractSkillsRemoteJob(RemoteOkJobDto remoteJob, Job targetJob) {
        StringBuilder description = new StringBuilder(remoteJob.getDescription());
        if (remoteJob.getTags() != null && !remoteJob.getTags().isEmpty()) {
            description.append("\n\nTags:");
            for (String tag : remoteJob.getTags()) description.append(" ").append(tag);
        }

        CompletableFuture<JobKnowledgeResponse> knowledgePredictionsFuture = jobSkillService.extractKnowledge(description.toString());
        CompletableFuture<JobSkillsResponse> skillsPredictionsFuture = jobSkillService.extractSkills(description.toString());

        CompletableFuture.allOf(knowledgePredictionsFuture, skillsPredictionsFuture).join();

        return updateJobWithAnalysis(targetJob, knowledgePredictionsFuture.join(), skillsPredictionsFuture.join());
    }



    @Override
    public Job analyzeSkillsMatching(Long jobId, Long userId, boolean forceAnalyze) {
        Profile profile = profileService.getProfileByUserId(userId);
        Job job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        if (!forceAnalyze && job.isSkillMatchingAnalyzed()) return job;
        job = analyzeIfNeeded(job, forceAnalyze);
        job.setSkillMatchingAnalysis(jobSkillService.analyzeSkillsMatching(job, profile.getUser()));
        job.setSkillMatchingAnalyzed(true);
        job.getSkillMatchingAnalysis().setJob(job);
        return jobRepository.save(job);
    }

    @Override
    public Job analyzeProjectsMatching(Long jobId, Long userId, boolean forceAnalyze) {
        Profile profile = profileService.getProfileByUserId(userId);
        Job job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        if (!forceAnalyze && job.isProjectMatchingAnalyzed()) return job;
        job = analyzeIfNeeded(job, forceAnalyze);
        job.setProjectMatchingAnalysis(jobSkillService.analyzeProjectsMatching(job, profile.getUser()));
        job.setProjectMatchingAnalyzed(true);
        job.getProjectMatchingAnalysis().setJob(job);
        return jobRepository.save(job);
    }

    @Override
    public Job getJobByIdAndUserId(Long jobId, Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);
        return jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId + " for user with id: " + userId));
    }

    @Override
    public void deleteJobByIdAndUserId(Long jobId, Long userId) {
        Job job = getJobByIdAndUserId(jobId, userId); // ensures ownership
        jobRepository.delete(job);
    }

    @Override
    public Job fullAnalyze(Long jobId, Long userId, boolean forceAnalyze) {
        Profile profile = profileService.getProfileByUserId(userId);
        Job job = analyzeSkillsMatching(jobId, userId, forceAnalyze);
        jobRepository.save(job);
        job = analyzeProjectsMatching(jobId, userId, forceAnalyze);
        job.setAnalyzeFailed(false);
        jobRepository.save(job);
        return jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
    }

    @Override
    public List<Job> getJobsByUserId(Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);
        return jobRepository.findJobsByProfileId(profile.getId());
    }

    private Job analyzeIfNeeded(Job job, boolean forceAnalyze) {
        if (job.isAnalyzed() && !forceAnalyze) return job;

        CompletableFuture<JobKnowledgeResponse> knowledgePredictionsFuture = jobSkillService.extractKnowledge(job.getDescription());
        CompletableFuture<JobSkillsResponse> skillsPredictionsFuture = jobSkillService.extractSkills(job.getDescription());

        CompletableFuture.allOf(knowledgePredictionsFuture, skillsPredictionsFuture).join();

        return jobRepository.save(updateJobWithAnalysis(job, knowledgePredictionsFuture.join(), skillsPredictionsFuture.join()));
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