package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.TailoredCvDto;
import com.Graduation.InstaCv.data.model.TailoredCv;
import com.Graduation.InstaCv.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TailoredCvMapper implements Mapper<TailoredCv, TailoredCvDto> {
    private final JobMapper jobMapper;

    @Override
    public TailoredCvDto mapTo(TailoredCv tailoredCv) {
        return TailoredCvDto.builder()
                .id(tailoredCv.getId())
                .userId(tailoredCv.getUserId())
                .job(jobMapper.mapTo(tailoredCv.getJob()))
                .personalDetails(tailoredCv.getPersonalDetails())
                .education(tailoredCv.getEducation())
                .experience(tailoredCv.getExperience())
                .skills(tailoredCv.getSkills())
                .projects(tailoredCv.getProjects())
                .summary(tailoredCv.getSummary())
                .createdAt(tailoredCv.getCreatedAt())
                .matchScore(tailoredCv.getMatchScore())
                .build();
    }

    @Override
    public TailoredCv mapFrom(TailoredCvDto tailoredCvDto) {
        return TailoredCv.builder()
                .id(tailoredCvDto.getId())
                .userId(tailoredCvDto.getUserId())
                .job(jobMapper.mapFrom(tailoredCvDto.getJob()))
                .personalDetails(tailoredCvDto.getPersonalDetails())
                .education(tailoredCvDto.getEducation())
                .experience(tailoredCvDto.getExperience())
                .skills(tailoredCvDto.getSkills())
                .projects(tailoredCvDto.getProjects())
                .summary(tailoredCvDto.getSummary())
                .createdAt(tailoredCvDto.getCreatedAt())
                .matchScore(tailoredCvDto.getMatchScore())
                .build();
    }
} 