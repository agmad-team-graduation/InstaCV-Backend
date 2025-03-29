package com.Graduation.InstaCv.data.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSkillsResponse {
    @JsonAlias("skills_predictions")
    private List<ExtractedJobSkillResponse> skillsPredictions;
}
