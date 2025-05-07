package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.RemoteOkJobDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;

import java.util.List;

public interface IJobService {
    Job addJob(Job job, Profile profile);
    List<Job> getJobsByUserId(Long userId);
    void backgroundFullAnalyzeJob(Long jobId, Long userId, Boolean forceAnalyze);

    Job extractSkillsRemoteJob(RemoteOkJobDto remoteJob, Job targetJob);

    Job analyzeSkillsMatching(Long jobId, Long userId, boolean forceAnalyze);
    Job analyzeProjectsMatching(Long jobId, Long userId, boolean forceAnalyze);
    Job getJobByIdAndUserId(Long jobId, Long userId);
    void deleteJobByIdAndUserId(Long jobId, Long userId);
    Job fullAnalyze(Long jobId, Long userId, boolean forceAnalyze);
}
