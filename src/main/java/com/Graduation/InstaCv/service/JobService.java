package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.data.model.JobSkill;
import com.Graduation.InstaCv.exceptions.JobNotFoundException;
//import com.Graduation.InstaCv.repository.JobAnalysisRepository;
import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.data.model.JobAnalysis;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    public CompletableFuture<Job> analyzeJob(Long jobId, boolean forceAnalyze) {
        return jobRepository.findById(jobId)
                .map(job -> analyzeIfNeeded(job, forceAnalyze))
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found"));
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
        if (job.isAnalyzed() && !forceAnalyze) return CompletableFuture.completedFuture(job);

        CompletableFuture<JobKnowledgeResponse> knowledgePredictions = jobSkillService.extractKnowledge(job.getDescription());
        CompletableFuture<JobSkillsResponse> skillsPredictions = jobSkillService.extractSkills(job.getDescription());

        return CompletableFuture.allOf(knowledgePredictions, skillsPredictions)
                .thenApply(v -> updateJobWithAnalysis(job, knowledgePredictions.join(), skillsPredictions.join()))
                .thenApply(jobRepository::save);
    }

    private Job updateJobWithAnalysis(Job job, JobKnowledgeResponse knowledge, JobSkillsResponse skills) {
        job.setJobAnalysis(
                JobAnalysis.builder()
                        .hardSkills(knowledge.getKnowledgePredictions().stream().map(jobSkillMapper::mapFrom).toList())
                        .softSkills(skills.getSkillsPredictions().stream().map(jobSkillMapper::mapFrom).toList())
                        .build());
        job.setAnalyzed(true);
        return job;
    }
}