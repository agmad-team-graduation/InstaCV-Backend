package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.model.cv.*;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TailoredCvDto {
    private Long id;
    private Long jobId;
    private PersonalDetails personalDetails;
    private String summary;
    private EducationSection educationSection;
    private ExperienceSection experienceSection;
    private SkillSection skillSection;
    private ProjectSection projectSection;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}