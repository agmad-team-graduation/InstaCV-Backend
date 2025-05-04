package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IJobService {
    Job addJob(Job job, Profile profile);
    List<Job> getJobsByUserId(Long userId);
    void backgroundFullAnalyzeJob(Long jobId, Long userId, Boolean forceAnalyze);

    CompletableFuture<Job> analyzeSkillExtractionAsync(Long jobId, Long userId, boolean forceAnalyze);

    CompletableFuture<Job> analyzeJobMatching(Long jobId, Long userId, boolean forceAnalyze);

    CompletableFuture<Job> analyzeProjectsMatching(Long jobId, Long userId, boolean forceAnalyze);

    Job getJobByIdAndUserId(Long jobId, Long userId);

    void deleteJobByIdAndUserId(Long jobId, Long userId);
}
