package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.jobMatching.projectMatching.ProjectsMatchingAnalysis;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.SkillMatchingAnalysis;
import com.Graduation.InstaCv.data.model.User;

import java.util.concurrent.CompletableFuture;

public interface IJobSkillService {
    CompletableFuture<JobKnowledgeResponse> extractKnowledge(String jobDescription);

    CompletableFuture<JobSkillsResponse> extractSkills(String jobDescription);

    SkillMatchingAnalysis analyzeSkillsMatching(Job job, User user);

    ProjectsMatchingAnalysis analyzeProjectsMatching(Job job, User user);
}
