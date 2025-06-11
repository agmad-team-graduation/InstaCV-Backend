package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.enums.JobSortField;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IJobService {
    Job addJob(Job job, Profile profile);

    Job fullAnalyze(Long jobId, Long userId, boolean isExternalJob, boolean forceAnalyze, boolean analyzeProjects);

    List<Job> getJobsByUserId(Long userId);

    Job getJobByIdAndUserId(Long jobId, Long userId);

    void deleteJobByIdAndUserId(Long jobId, Long userId);

    Page<Job> getRecommendedExternalJobsPaginated(Long profileId, Pageable pageable, JobSortField sortField);
}
