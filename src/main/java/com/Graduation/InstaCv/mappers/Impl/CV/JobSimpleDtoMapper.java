package com.Graduation.InstaCv.mappers.Impl.CV;

import com.Graduation.InstaCv.data.dto.JobSimpleDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import org.springframework.stereotype.Component;

@Component
public class JobSimpleDtoMapper implements ContextAwareMapper<Job, JobSimpleDto, Profile> {
    @Override
    public JobSimpleDto mapTo(Job job) {
        return JobSimpleDto.builder()
                .id(job.getId())
                .profileId(job.getProfile().getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .description(job.getDescription())
                .build();
    }

    @Override
    public Job mapFrom(JobSimpleDto jobSimpleDto, Profile profile) {
        return Job.builder()
                .id(jobSimpleDto.getId())
                .profile(profile)
                .title(jobSimpleDto.getTitle())
                .company(jobSimpleDto.getCompany())
                .description(jobSimpleDto.getDescription())
                .build();
    }
}
