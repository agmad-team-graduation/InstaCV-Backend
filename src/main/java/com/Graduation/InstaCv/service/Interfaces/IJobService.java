package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.data.model.job.Job;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IJobService {
    Job addJob(Job job, Profile profile);

    Job getJob(Long jobId);

    List<Job> getJobs();

    void delete(Long jobId);

    CompletableFuture<Job> analyzeJob(Long jobId, boolean forceAnalyze);

    Job analyzeJobMatching(Long jobId, Long userId);

    Job analyzeProjectsMatching(Long jobId, Long userId);
}
