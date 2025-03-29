package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.request.JobSkillExtractionRequest;
import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.gateways.JobSkillExtractionClient;
import com.Graduation.InstaCv.service.Interfaces.IJobSkillService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class JobSkillService implements IJobSkillService {
    private final JobSkillExtractionClient jobSkillClient;

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
}