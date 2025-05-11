package com.Graduation.InstaCv.mappers.impl.jobs;


import com.Graduation.InstaCv.data.dto.ScrapedJobSimpleDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScrapedJobSimpleDtoMapper implements Mapper<Job, ScrapedJobSimpleDto> {
    @Override
    public ScrapedJobSimpleDto mapTo(Job job) {
        return ScrapedJobSimpleDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .description(job.getDescription())
                .applyUrl(job.getRemoteJobData().getApplyUrl())
                .date(job.getRemoteJobData().getDate())
                .build();
    }

    @Override
    public Job mapFrom(ScrapedJobSimpleDto scrapedJobSimpleDto) {
        throw new UnsupportedOperationException("Not implemented yet, and not needed");
    }
}
