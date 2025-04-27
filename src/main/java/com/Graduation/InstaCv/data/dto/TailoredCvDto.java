package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.model.cv.EducationCv;
import com.Graduation.InstaCv.data.model.cv.ExperienceCv;
import com.Graduation.InstaCv.data.model.cv.ProjectCv;
import com.Graduation.InstaCv.data.model.cv.skills.UserSkillCv;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
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
    private Long profileId;
    private Long jobId;
    private PersonalDetails personalDetails;
    private List<EducationCv> education;
    private List<ExperienceCv> experience;
    private List<UserSkillCv> skills;
    private List<ProjectCv> projects;
    private String summary;
    private LocalDateTime createdAt;
}