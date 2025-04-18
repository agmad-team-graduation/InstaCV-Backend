package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.model.CV.EducationCv;
import com.Graduation.InstaCv.data.model.CV.ExperienceCv;
import com.Graduation.InstaCv.data.model.CV.ProjectCv;
import com.Graduation.InstaCv.data.model.CV.skills.UserSkillCv;
import com.Graduation.InstaCv.data.model.profile.Education;
import com.Graduation.InstaCv.data.model.profile.Experience;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
import com.Graduation.InstaCv.data.model.profile.UserSkill;
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
    private List<EducationCv> education;
    private List<ExperienceCv> experience;
    private List<UserSkillCv> skills;
    private List<ProjectCv> projects;
    private String summary;
    private LocalDateTime createdAt;
}