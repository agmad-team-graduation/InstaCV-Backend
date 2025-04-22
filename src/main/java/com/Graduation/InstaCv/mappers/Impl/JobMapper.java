package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobMapper implements Mapper<Job, JobDto> {
    private ProfileRepository profileRepository;

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
    public Job mapFrom(JobDto jobDto) {
        return Job.builder()
                .id(jobDto.getId())
                .profile(profileRepository.findById(jobDto.getProfileId()).orElse(null))
                .title(jobDto.getTitle())
                .company(jobDto.getCompany())
                .description(jobDto.getDescription())
                .isAnalyzed(jobDto.isAnalyzed())
                .build();
    }
}