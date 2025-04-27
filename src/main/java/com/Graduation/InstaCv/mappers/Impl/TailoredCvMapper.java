package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.TailoredCvDto;
import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TailoredCvMapper implements ContextAwareMapper<TailoredCv, TailoredCvDto, Job> {
    @Override
    public TailoredCvDto mapTo(TailoredCv tailoredCv) {
        return TailoredCvDto.builder()
                .id(tailoredCv.getId())
                .profileId(tailoredCv.getProfile().getId())
                .jobId(tailoredCv.getJob().getId())
                .personalDetails(tailoredCv.getPersonalDetails())
                .education(tailoredCv.getEducation())
                .experience(tailoredCv.getExperience())
                .skills(tailoredCv.getSkills())
                .projects(tailoredCv.getProjects())
                .summary(tailoredCv.getSummary())
                .createdAt(tailoredCv.getCreatedAt())
                .build();
    }

    @Override
    public TailoredCv mapFrom(TailoredCvDto tailoredCvDto, Job job) {
        return TailoredCv.builder()
                .id(tailoredCvDto.getId())
                .profile(job.getProfile())
                .job(job)
                .personalDetails(tailoredCvDto.getPersonalDetails())
                .education(tailoredCvDto.getEducation())
                .experience(tailoredCvDto.getExperience())
                .skills(tailoredCvDto.getSkills())
                .projects(tailoredCvDto.getProjects())
                .summary(tailoredCvDto.getSummary())
                .createdAt(tailoredCvDto.getCreatedAt())
                .build();
    }
}