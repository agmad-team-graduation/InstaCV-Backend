package com.Graduation.InstaCv.data.dto.request;

import com.Graduation.InstaCv.data.model.JobSkill;
import com.Graduation.InstaCv.data.model.profile.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectsMatchingRequest {
    List<JobSkill> jobSkills;
    List<Project> projects;
    Float similarityThreshold;
}
