package com.Graduation.InstaCv.mappers.impl.jobs;

import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.enums.AnalyzeStatus;
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
        JobDto res = JobDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .description(job.getDescription())
                .isAnalyzed(job.getCompleteAnalysisStatus() == AnalyzeStatus.COMPLETED)
                .jobSkills(job.getJobSkills())
                .isSkillMatchingAnalyzed(job.getCompleteAnalysisStatus() == AnalyzeStatus.COMPLETED)
                .skillMatchingAnalysis(
                        job.getSkillMatchingAnalyses().stream().findFirst().orElse(null)
                )
                .build();
        if (job.getProfile() != null) {
            res.setProfileId(job.getProfile().getId());
        }
        return res;
    }

    @Override
    public Job mapFrom(JobDto jobDto, Profile profile) {
        throw new UnsupportedOperationException("Not implemented yet, and not needed");
    }
}