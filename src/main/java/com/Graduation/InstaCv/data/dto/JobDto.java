package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.model.JobAnalysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
    private Long id;
    private String title;
    private String company;
    private String description;
    private boolean isAnalyzed = false;
    private JobAnalysis jobAnalysis;
}
