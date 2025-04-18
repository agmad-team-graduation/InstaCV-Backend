package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.data.model.ProjectsMatchingAnalysis;
import com.Graduation.InstaCv.data.model.SkillMatchingAnalysis;
import com.Graduation.InstaCv.data.model.User;

import java.util.concurrent.CompletableFuture;

public interface IJobSkillService {
    CompletableFuture<JobKnowledgeResponse> extractKnowledge(String jobDescription);

    CompletableFuture<JobSkillsResponse> extractSkills(String jobDescription);

    SkillMatchingAnalysis analyzeSkillsMatching(Job job, User user);

    ProjectsMatchingAnalysis analyzeProjectsMatching(Job job, User user);
}
