package com.Graduation.InstaCv.data.dto;

import com.Graduation.InstaCv.data.model.profile.*;

import java.util.List;

public class CvDto {
    private String userId;
    private ProfileDto profile;
    private List<Education> education;
    private List<Experience> experience;
    private List<Skill> skills;
    private List<Project> projects;
    private JobDto job;
}
