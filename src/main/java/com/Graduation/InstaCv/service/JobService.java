package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.exceptions.JobNotFoundException;
//import com.Graduation.InstaCv.repository.JobAnalysisRepository;
import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.data.model.JobAnalysis;
import com.Graduation.InstaCv.repository.JobRepository;
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
    private final JobSkillService jobSkillService;

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
    public CompletableFuture<Job> analyzeJob(Long jobId) {
        return jobRepository.findById(jobId)
                .map(this::analyzeIfNeeded)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found"));
    }

    private CompletableFuture<Job> analyzeIfNeeded(Job job) {
        if (job.isAnalyzed()) return CompletableFuture.completedFuture(job);

        CompletableFuture<JobKnowledgeResponse> knowledgePredictions = jobSkillService.extractKnowledge(job.getDescription());
        CompletableFuture<JobSkillsResponse> skillsPredictions = jobSkillService.extractSkills(job.getDescription());

        return CompletableFuture.allOf(knowledgePredictions, skillsPredictions)
                .thenApply(v -> updateJobWithAnalysis(job, knowledgePredictions.join().getKnowledgePredictions(),
                        skillsPredictions.join().getSkillsPredictions()))
                .thenApply(jobRepository::save);
    }

    private Job updateJobWithAnalysis(Job job, List<String> hardSkills, List<String> softSkills) {
        job.setJobAnalysis(JobAnalysis.builder()
                .hardSkills(hardSkills)
                .softSkills(softSkills)
                .build());
        job.setAnalyzed(true);
        return job;
    }
}