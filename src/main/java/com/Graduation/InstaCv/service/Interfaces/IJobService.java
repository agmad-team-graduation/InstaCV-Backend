package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.exceptions.JobNotFoundException;
import com.Graduation.InstaCv.data.model.Job;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IJobService {
    Job addJob(Job job);
    Job getJob(Long jobId);
    List<Job> getJobs();
    void delete(Long jobId);
    CompletableFuture<Job> analyzeJob(Long jobId, boolean forceAnalyze) throws JobNotFoundException;
}
