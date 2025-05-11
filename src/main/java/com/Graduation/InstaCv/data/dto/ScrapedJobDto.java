package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.SkillMatchingAnalysis;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedJobDto {
    private Long id;
    private String title;
    private String company;
    private String description;
    private String htmlDescription;
    private String applyUrl;
    private OffsetDateTime date;
    private boolean isAnalyzed;
    private SkillMatchingAnalysis skillMatchingAnalysis;
    @JsonIgnore
    @Builder.Default
    private List<JobSkill> jobSkills = List.of();

    public List<BaseSkillDto> getHardSkills() {
        return jobSkills.stream()
                .filter(jobSkill -> jobSkill.getSkillType() == SkillType.HARD)
                .map(jobSkill -> BaseSkillDto.builder()
                        .id(jobSkill.getId())
                        .skill(jobSkill.getSkill())
                        .build())
                .toList();
    }

    public List<BaseSkillDto> getSoftSkills() {
        return jobSkills.stream()
                .filter(jobSkill -> jobSkill.getSkillType() == SkillType.SOFT)
                .map(jobSkill -> BaseSkillDto.builder()
                        .id(jobSkill.getId())
                        .skill(jobSkill.getSkill())
                        .build())
                .toList();
    }
}
