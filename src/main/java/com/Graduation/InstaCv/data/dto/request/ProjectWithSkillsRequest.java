package com.Graduation.InstaCv.data.dto.request;

import com.Graduation.InstaCv.data.dto.BaseSkillDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProjectWithSkillsRequest {
    private Long id;
    private List<BaseSkillDto> skills;
}
