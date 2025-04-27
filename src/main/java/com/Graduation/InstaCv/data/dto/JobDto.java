package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.SkillMatchingAnalysis;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
    private Long id;
    private Long profileId;
    private String title;
    private String company;
    private String description;
    private boolean isAnalyzed;
    private boolean isSkillMatchingAnalyzed;
    private SkillMatchingAnalysis skillMatchingAnalysis;
    @JsonIgnore
    private List<JobSkill> jobSkills = List.of();
    public List<JobSkill> getHardSkills() {
        return jobSkills.stream()
                .filter(jobSkill -> jobSkill.getSkillType() == SkillType.HARD)
                .toList();
    }
    public List<JobSkill> getSoftSkills() {
        return jobSkills.stream()
                .filter(jobSkill -> jobSkill.getSkillType() == SkillType.SOFT)
                .toList();
    }
}
