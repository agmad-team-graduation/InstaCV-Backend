package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.BaseSkillDto;
import com.Graduation.InstaCv.data.dto.request.JobSkillExtractionRequest;
//import com.Graduation.InstaCv.data.dto.request.MatchingSkillsRequest;
import com.Graduation.InstaCv.data.dto.request.MatchingSkillsRequest;
import com.Graduation.InstaCv.data.dto.request.ProjectWithSkillsRequest;
import com.Graduation.InstaCv.data.dto.request.ProjectsMatchingRequest;
import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.data.model.*;
import com.Graduation.InstaCv.data.model.jobMatching.projectMatching.ProjectsMatchingAnalysis;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.SkillMatchingAnalysis;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Project;
import com.Graduation.InstaCv.gateways.JobSkillExtractionClient;
import com.Graduation.InstaCv.gateways.SkillMatchingClient;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.service.Interfaces.IJobSkillService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class JobSkillService implements IJobSkillService {
    private final JobSkillExtractionClient jobSkillClient;
    private final SkillMatchingClient skillMatchingClient;
    private final Mapper<BaseSkill, BaseSkillDto> jobSkillMapper;
    private final Mapper<Project, ProjectWithSkillsRequest> projectMapper;

    @Override
    public CompletableFuture<JobKnowledgeResponse> extractKnowledge(String description) {
        return CompletableFuture.supplyAsync(() ->
                jobSkillClient.extractKnowledge(new JobSkillExtractionRequest(description))
        );
    }

    @Override
    public CompletableFuture<JobSkillsResponse> extractSkills(String description) {
        return CompletableFuture.supplyAsync(() ->
                jobSkillClient.extractSkills(new JobSkillExtractionRequest(description))
        );
    }

    @Override
    public SkillMatchingAnalysis analyzeSkillsMatching(Job job, User user) {
        MatchingSkillsRequest request = MatchingSkillsRequest.builder()
                .jobSkills(job.getHardSkills().stream().map(jobSkillMapper::mapTo).toList())
                .userSkills(user.getProfile().getUserSkills().stream().map(jobSkillMapper::mapTo).toList())
                .similarityThreshold(0.7f).build();
        SkillMatchingAnalysis skillMatchingAnalysis = skillMatchingClient.matchSkills(request);
        skillMatchingAnalysis.setJob(job);
        skillMatchingAnalysis.getMatchedSkills().forEach(matchedSkill -> matchedSkill.setSkillMatchingAnalysis(skillMatchingAnalysis));
        return skillMatchingAnalysis;
    }

    @Override
    public ProjectsMatchingAnalysis analyzeProjectsMatching(Job job, User user) {
        ProjectsMatchingRequest request = ProjectsMatchingRequest.builder()
                .jobSkills(job.getHardSkills().stream().map(jobSkillMapper::mapTo).toList())
                .projects(user.getProfile().getProjects().stream().map(projectMapper::mapTo).toList())
                .similarityThreshold(0.7f).build();
        ProjectsMatchingAnalysis projectsMatchingAnalysis = skillMatchingClient.matchProjectsSkills(request);
        projectsMatchingAnalysis.getAllAnalyzedProjects().forEach(matchedProject -> {
            matchedProject.setProjectsMatchingAnalysis(projectsMatchingAnalysis);
            matchedProject.getMatchedSkills().forEach(matchedSkill -> matchedSkill.setMatchedProject(matchedProject));
        });
        return projectsMatchingAnalysis;
    }
}