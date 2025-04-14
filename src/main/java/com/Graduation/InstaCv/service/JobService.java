package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.response.ExtractedJobSkillResponse;
import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.data.model.*;
import com.Graduation.InstaCv.exceptions.JobNotFoundException;
//import com.Graduation.InstaCv.repository.JobAnalysisRepository;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final JobSkillService jobSkillService;
    private final Mapper<JobSkill, ExtractedJobSkillResponse> jobSkillMapper;

    @Override
    public Job addJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Job getJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found"));
    }

    @Override
    @Async
    public CompletableFuture<Job> analyzeJob(Long jobId, boolean forceAnalyze) {
        return jobRepository.findById(jobId)
                .map(job -> analyzeIfNeeded(job, forceAnalyze))
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found"));
    }

    @Override
    public Job AnalyzeJobMatching(Long jobId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        analyzeIfNeeded(job, false);
        job.setSkillMatchingAnalysis(jobSkillService.analyzeMatching(job, user));
        job.setIsMatchingAnalyzed(true);
        return jobRepository.save(job);
    }

    @Override
    public List<Job> getJobs() {
        return jobRepository.findAll();
    }

    @Override
    public void delete(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    private CompletableFuture<Job> analyzeIfNeeded(Job job, boolean forceAnalyze) {
        if (job.getIsAnalyzed() && !forceAnalyze) return CompletableFuture.completedFuture(job);

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
        List<JobSkill> softSkills = skills.getSkillsPredictions().stream()
                .map(jobSkillMapper::mapFrom)
                .toList();
        // TODO: Think about handling soft skills
        job.setJobSkills(hardSkills);
        job.setIsAnalyzed(true);
        return job;
    }
}