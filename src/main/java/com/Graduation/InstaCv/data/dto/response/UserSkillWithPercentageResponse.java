package com.Graduation.InstaCv.data.dto.response;

import com.Graduation.InstaCv.data.enums.SkillLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillWithPercentageResponse {
    private Long id;
    private String skill;
    private SkillLevel level;
    private Double marketDemandPercentage;
} 