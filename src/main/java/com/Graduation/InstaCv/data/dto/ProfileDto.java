package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.data.model.profile.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private Long userId;
    private PersonalDetails personalDetails;
    private List<Education> educationList;
    private List<Experience> experienceList;
    private List<UserSkill> userSkills;
    private List<Project> projects;
    private List<Job> addedJobs;
}
