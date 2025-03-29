package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.model.profile.Education;
import com.Graduation.InstaCv.data.model.profile.Experience;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
import com.Graduation.InstaCv.data.model.profile.Skill;
import com.Graduation.InstaCv.data.model.profile.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TailoredCvDto {
    private Long id;
    private Long userId;
    private JobDto job;
    private PersonalDetails personalDetails;
    private List<Education> education;
    private List<Experience> experience;
    private List<Skill> skills;
    private List<Project> projects;
    private String summary;
    private LocalDateTime createdAt;
    private Integer matchScore;
} 