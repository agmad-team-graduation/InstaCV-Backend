package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class JobMapper implements Mapper<Job, JobDto> {
    @Override
    public JobDto mapTo(Job job) {
        return JobDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .description(job.getDescription())
                .isAnalyzed(job.isAnalyzed())
                .jobAnalysis(job.getJobAnalysis())
                .build();
    }

    @Override
    public Job mapFrom(JobDto jobDto) {
        return Job.builder()
                .id(jobDto.getId())
                .title(jobDto.getTitle())
                .company(jobDto.getCompany())
                .description(jobDto.getDescription())
                .isAnalyzed(jobDto.isAnalyzed())
                .jobAnalysis(jobDto.getJobAnalysis())
                .build();
    }
} 