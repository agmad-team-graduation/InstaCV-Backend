package com.Graduation.InstaCv.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionsResponse {
    
    private Long jobId;
    private String jobTitle;
    private String company;
    private List<InterviewQuestion> questions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewQuestion {
        private String question;
        private String category;
        private String difficulty;
        private String expectedAnswer;
    }
} 