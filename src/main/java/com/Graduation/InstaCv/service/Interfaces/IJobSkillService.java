package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;

import java.util.concurrent.CompletableFuture;

public interface IJobSkillService {
    CompletableFuture<JobKnowledgeResponse> extractKnowledge(String jobDescription);
    CompletableFuture<JobSkillsResponse> extractSkills(String jobDescription);
}
