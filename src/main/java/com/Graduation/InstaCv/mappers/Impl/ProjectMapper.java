package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.BaseSkillDto;
import com.Graduation.InstaCv.data.dto.request.ProjectWithSkillsRequest;
import com.Graduation.InstaCv.data.model.BaseSkill;
import com.Graduation.InstaCv.data.model.profile.Project;
import com.Graduation.InstaCv.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class ProjectMapper implements Mapper<Project, ProjectWithSkillsRequest> {
    private final Mapper<BaseSkill, BaseSkillDto> baseSkillMapper;

    @Override
    public ProjectWithSkillsRequest mapTo(Project project) {
        if (project == null)
            return null;
        return ProjectWithSkillsRequest.builder()
                .id(project.getId())
                .skills(project.getSkills().stream().map(baseSkillMapper::mapTo).toList())
                .build();
    }

    @Override
    public Project mapFrom(ProjectWithSkillsRequest projectWithSkillsRequest) {
        if (projectWithSkillsRequest == null)
            return null;
        return Project.builder()
                .id(projectWithSkillsRequest.getId())
                .skills(projectWithSkillsRequest.getSkills().stream().map(
                        skill -> baseSkillMapper.mapFrom(skill).asProjectSkill()
                ).toList())
                .build();
    }
}
