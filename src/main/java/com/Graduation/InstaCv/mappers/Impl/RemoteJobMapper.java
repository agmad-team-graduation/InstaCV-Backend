package com.Graduation.InstaCv.mappers.Impl;


import com.Graduation.InstaCv.data.dto.RemoteOkJobDto;
import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.job.RemoteJobData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemoteJobMapper {

    /**

     * Converts a RemoteOkJobDto to a Job entity with associated RemoteJobData and JobSkills

     */

    public Job toJobEntity(RemoteOkJobDto dto) {
        // Create Job entity first (without remoteJobData to prevent circular reference)
        Job job = Job.builder()
                .title(dto.getTitle())
                .company(dto.getCompany())
                .description(dto.getDescription())
                .jobSkills(new ArrayList<>())
                .isAnalyzed(false)
                .isSkillMatchingAnalyzed(false)
                .isProjectMatchingAnalyzed(false)
                .analyzeFailed(false)
                .build();
        job.setProfile(null); // Set profile to null for remote jobs
        // Create RemoteJobData entity
        RemoteJobData remoteJobData = RemoteJobData.builder()
                .remoteId(dto.getId())
                .applyUrl(dto.getApplyUrl())
                .date(dto.getDate())
                .job(job)
                .build();
        // Set remoteJobData to job
        job.setRemoteJobData(remoteJobData);
        // Add tags as skills
        List<JobSkill> skills = new ArrayList<>();
        if (dto.getTags() != null) {
            for (String tagValue : dto.getTags()) {
                JobSkill skill = JobSkill.builder()
                        .skill(tagValue)
                        .skillType(SkillType.HARD)
                        .job(job)
                        .modelConfidence(1.0f)
                        .build();
                skills.add(skill);
            }
        }
        job.setJobSkills(skills);
        return job;
    }
    /**
     * Converts a Job entity back to a RemoteOkJobDto
     */
    public RemoteOkJobDto toRemoteOkJobDto(Job job) {
        RemoteJobData remoteData = job.getRemoteJobData();
        if (remoteData == null) {
            return null; // This isn't a remote job
        }

        RemoteOkJobDto dto = new RemoteOkJobDto();
        dto.setId(remoteData.getRemoteId());
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setApplyUrl(remoteData.getApplyUrl());
        dto.setDescription(job.getDescription());
        dto.setDate(remoteData.getDate());

        // Extract skills as tags
        List<String> tags = job.getJobSkills().stream()
                .map(JobSkill::getSkill)
                .collect(Collectors.toList());
        dto.setTags(tags);

        return dto;
    }
}