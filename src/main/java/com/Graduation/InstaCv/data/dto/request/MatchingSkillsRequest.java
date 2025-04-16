package com.Graduation.InstaCv.data.dto.request;

import com.Graduation.InstaCv.data.dto.BaseSkillDto;
import com.Graduation.InstaCv.data.model.JobSkill;
import com.Graduation.InstaCv.data.model.profile.UserSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingSkillsRequest {
    List<BaseSkillDto> jobSkills;
    List<BaseSkillDto> userSkills;
    Float similarityThreshold;
}
