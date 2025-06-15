package com.Graduation.InstaCv.mappers.impl.cv;

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
                "skill", tailoredCv.getSkillSection().getOrderIndex(),
                "summary", tailoredCv.getSummarySection().getOrderIndex()
        );
        TailoredCvDto.TailoredCvDtoBuilder tailoredCvDtoBuilder = TailoredCvDto.builder()
                .id(tailoredCv.getId())
                .jobId(tailoredCv.getJob() == null ? null : tailoredCv.getJob().getId())
                .cvTitle(tailoredCv.getCvTitle())
                .personalDetails(tailoredCv.getPersonalDetails())
                .educationSection(tailoredCv.getEducationSection())
                .experienceSection(tailoredCv.getExperienceSection())
                .skillSection(tailoredCv.getSkillSection())
                .projectSection(tailoredCv.getProjectSection())
                .summarySection(tailoredCv.getSummarySection())
                .createdAt(tailoredCv.getCreatedAt())
                .updatedAt(tailoredCv.getUpdatedAt())
                .sectionsOrder(sectionsOrder);
        return tailoredCvDtoBuilder.build();
    }

    @Override
    public TailoredCv mapFrom(TailoredCvDto tailoredCvDto, Job job) {
        return TailoredCv.builder()
                .id(tailoredCvDto.getId())
                .cvTitle(tailoredCvDto.getCvTitle())
                .profile(job.getProfile())
                .job(job)
                .personalDetails(tailoredCvDto.getPersonalDetails())
                .educationSection(tailoredCvDto.getEducationSection())
                .experienceSection(tailoredCvDto.getExperienceSection())
                .skillSection(tailoredCvDto.getSkillSection())
                .projectSection(tailoredCvDto.getProjectSection())
                .summarySection(tailoredCvDto.getSummarySection())
                .createdAt(tailoredCvDto.getCreatedAt())
                .updatedAt(tailoredCvDto.getUpdatedAt())
                .build();
    }
}