package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.model.job.Job;

import java.util.List;

public interface IScarpedJobService {
    Job getJobById(Long id);
    List<Job> getRecommendedJobs(Long userId);
}
