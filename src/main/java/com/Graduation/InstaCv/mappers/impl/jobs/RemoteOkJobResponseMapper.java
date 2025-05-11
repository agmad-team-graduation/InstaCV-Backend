package com.Graduation.InstaCv.mappers.impl.jobs;


import com.Graduation.InstaCv.data.dto.RemoteOkJobResponse;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.job.RemoteJobData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RemoteOkJobResponseMapper {

    /**
     * Converts a RemoteOkJobDto to a Job entity with associated RemoteJobData and JobSkills
     */

    public Job toJobEntity(RemoteOkJobResponse dto) {
        // Create Job entity first (without remoteJobData to prevent circular reference)
        Job job = Job.builder()
                .title(dto.getTitle())
                .profile(null)
                .company(dto.getCompany())
                .description(dto.getDescription())
                .jobSkills(new ArrayList<>())
                .remoteJobData(
                        RemoteJobData.builder()
                                .remoteId(dto.getId())
                                .applyUrl(dto.getApplyUrl())
                                .htmlDescription(dto.getHtmlDescription())
                                .date(dto.getDate())
                                .tags(dto.getTags())
                                .build()
                )
                .build();
        job.getRemoteJobData().setJob(job);
        return job;
    }

    public RemoteOkJobResponse toRemoteOkJobDto(Job job) {
        throw new UnsupportedOperationException("Not implemented yet, and not needed");
    }
}