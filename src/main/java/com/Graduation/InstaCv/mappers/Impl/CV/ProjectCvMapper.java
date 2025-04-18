package com.Graduation.InstaCv.mappers.Impl.CV;

import com.Graduation.InstaCv.data.model.BaseSkill;
import com.Graduation.InstaCv.data.model.CV.ProjectCv;
import com.Graduation.InstaCv.data.model.CV.skills.ProjectSkillCv;
import com.Graduation.InstaCv.data.model.ProjectSkill;
import com.Graduation.InstaCv.data.model.profile.Project;
import com.Graduation.InstaCv.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectCvMapper implements Mapper<ProjectCv, Project> {
    @Override
    public Project mapTo(ProjectCv projectCv) {
        Project project = Project.builder()
                .title(projectCv.getTitle())
                .description(projectCv.getDescription())
                .startDate(projectCv.getStartDate())
                .endDate(projectCv.getEndDate())
                .isPresent(projectCv.isPresent())
                .build();
        project.setSkills(
                projectCv.getSkills().stream().map(
                        skill -> {
                            ProjectSkill projectSkill = BaseSkill.builder()
                                    .skill(skill.getSkill())
                                    .build().asProjectSkill();
                            projectSkill.setProject(project);
                            return projectSkill;
                        }
                ).toList()
        );
        return project;
    }

    @Override
    public ProjectCv mapFrom(Project project) {
        ProjectCv projectCv = ProjectCv.builder()
                .title(project.getTitle())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .isPresent(project.isPresent())
                .build();
        projectCv.setSkills(
                project.getSkills().stream().map(
                        skill -> ProjectSkillCv.builder().skill(skill.getSkill()).projectCv(projectCv).build()
                ).toList()
        );
        return projectCv;
    }
}
