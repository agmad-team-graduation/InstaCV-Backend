package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.request.JobDescriptionSkillExtractionRequest;
import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.service.Interfaces.IJobSkillService;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class JobSkillService implements IJobSkillService {
    private final WebClient webClient = WebClient.create("http://localhost:8000");

    @Override
    public CompletableFuture<JobKnowledgeResponse> extractKnowledge(String description) {
        return webClient.post()
                .uri("/predict_knowledge")
                .bodyValue(new JobDescriptionSkillExtractionRequest(description))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<JobKnowledgeResponse>() {
                })
                .toFuture();
    }

    @Override
    public CompletableFuture<JobSkillsResponse> extractSkills(String description) {
        return webClient.post()
                .uri("/predict_skills")
                .bodyValue(new JobDescriptionSkillExtractionRequest(description))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<JobSkillsResponse>() {
                })
                .toFuture();
    }
}