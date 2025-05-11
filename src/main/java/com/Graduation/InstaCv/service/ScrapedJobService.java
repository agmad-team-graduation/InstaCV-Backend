package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.service.Interfaces.IScarpedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapedJobService implements IScarpedJobService {
    private final JobRepository jobRepository;
    private final ProfileService profileService;
    private final JobService jobService;
    @Override
    public Job getJobById(Long id) {
        return jobRepository.findJobByIdAndProfileIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id " + id));
    }

    @Override
    public List<Job> getRecommendedJobs(Long profileId) {
        return jobRepository.findAnalyzedScrapedJobsByProfileId(profileId);
    }
}
