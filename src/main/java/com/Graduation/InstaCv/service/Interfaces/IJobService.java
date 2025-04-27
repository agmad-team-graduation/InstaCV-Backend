package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.model.job.Job;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IJobService {
    Job addJob(Long userId, Job job);

    List<Job> getJobsByUserId(Long userId);


    CompletableFuture<Job> analyzeJob(Long jobId, Long userId, boolean forceAnalyze);

    Job analyzeJobMatching(Long jobId, Long userId, boolean forceAnalyze);

    Job analyzeProjectsMatching(Long jobId, Long userId);

    Job getJobByIdAndUserId(Long jobId, Long userId);

    void deleteJobByIdAndUserId(Long jobId, Long userId);
}
