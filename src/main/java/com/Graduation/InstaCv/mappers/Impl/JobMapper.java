package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobMapper implements ContextAwareMapper<Job, JobDto, Profile> {
    @Override
    public JobDto mapTo(Job job) {
        return JobDto.builder()
                .id(job.getId())
                .profileId(job.getProfile().getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .description(job.getDescription())
                .isAnalyzed(job.isAnalyzed())
                .build();
    }

    @Override
    public Job mapFrom(JobDto jobDto, Profile profile) {
        return Job.builder()
                .id(jobDto.getId())
                .profile(profile)
                .title(jobDto.getTitle())
                .company(jobDto.getCompany())
                .description(jobDto.getDescription())
                .isAnalyzed(jobDto.isAnalyzed())
                .build();
    }
}