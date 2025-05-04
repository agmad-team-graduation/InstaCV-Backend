package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.TailoredCvDto;
import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class TailoredCvMapper implements ContextAwareMapper<TailoredCv, TailoredCvDto, Job> {
    @Override
    public TailoredCvDto mapTo(TailoredCv tailoredCv) {
        Map<String, Integer> sectionsOrder = Map.of(
                "education", tailoredCv.getEducationSection().getOrderIndex(),
                "experience", tailoredCv.getExperienceSection().getOrderIndex(),
                "project", tailoredCv.getProjectSection().getOrderIndex(),
                "skill", tailoredCv.getSkillSection().getOrderIndex()
        );
        return TailoredCvDto.builder()
                .id(tailoredCv.getId())
                .jobId(tailoredCv.getJob().getId())
                .personalDetails(tailoredCv.getPersonalDetails())
                .educationSection(tailoredCv.getEducationSection())
                .experienceSection(tailoredCv.getExperienceSection())
                .skillSection(tailoredCv.getSkillSection())
                .projectSection(tailoredCv.getProjectSection())
                .summary(tailoredCv.getSummary())
                .createdAt(tailoredCv.getCreatedAt())
                .updatedAt(tailoredCv.getUpdatedAt())
                .sectionsOrder(sectionsOrder)
                .build();
    }

    @Override
    public TailoredCv mapFrom(TailoredCvDto tailoredCvDto, Job job) {
        return TailoredCv.builder()
                .id(tailoredCvDto.getId())
                .profile(job.getProfile())
                .job(job)
                .personalDetails(tailoredCvDto.getPersonalDetails())
                .educationSection(tailoredCvDto.getEducationSection())
                .experienceSection(tailoredCvDto.getExperienceSection())
                .skillSection(tailoredCvDto.getSkillSection())
                .projectSection(tailoredCvDto.getProjectSection())
                .summary(tailoredCvDto.getSummary())
                .createdAt(tailoredCvDto.getCreatedAt())
                .updatedAt(tailoredCvDto.getUpdatedAt())
                .build();
    }
}