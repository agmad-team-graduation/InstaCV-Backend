package com.Graduation.InstaCv.data.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionsRequest {
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    @NotNull(message = "Number of questions is required")
    @Min(value = 1, message = "Number of questions must be at least 1")
    @Max(value = 20, message = "Number of questions cannot exceed 20")
    private Integer numberOfQuestions;
} 