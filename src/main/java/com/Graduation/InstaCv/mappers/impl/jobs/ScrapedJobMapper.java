package com.Graduation.InstaCv.mappers.impl.jobs;

import com.Graduation.InstaCv.data.dto.ScrapedJobDto;
import com.Graduation.InstaCv.data.enums.AnalyzeStatus;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScrapedJobMapper implements Mapper<Job, ScrapedJobDto> {
    @Override
    public ScrapedJobDto mapTo(Job job) {
        throw new UnsupportedOperationException("Not implemented yet, and not needed");
    }

    public ScrapedJobDto mapTo(Job job, Profile profile) {
        return ScrapedJobDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .description(job.getDescription())
                .isAnalyzed(job.getSkillExtractionStatus() == AnalyzeStatus.COMPLETED)
                .jobSkills(job.getJobSkills())
                .applyUrl(job.getRemoteJobData().getApplyUrl())
                .date(job.getRemoteJobData().getDate())
                .skillMatchingAnalysis(
                        job.getSkillMatchingAnalyses().stream().filter(
                                skillMatchingAnalysis -> skillMatchingAnalysis.getProfile().getId().equals(profile.getId())
                        ).findFirst().orElse(null)
                )
                .build();
    }

    @Override
    public Job mapFrom(ScrapedJobDto jobDto) {
        throw new UnsupportedOperationException("Not implemented yet, and not needed");
    }
}