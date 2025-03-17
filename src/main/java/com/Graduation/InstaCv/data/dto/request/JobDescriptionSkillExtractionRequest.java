package com.Graduation.InstaCv.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDescriptionSkillExtractionRequest {
    private String jobDescription;
}
