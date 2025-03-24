package com.Graduation.InstaCv.gateways;

import com.Graduation.InstaCv.data.dto.request.JobSkillExtractionRequest;
import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "JobSkillExtractionClient", url = "${job.skill.extraction.url}")
public interface JobSkillExtractionClient {
    @PostMapping("/predict_knowledge")
    JobKnowledgeResponse extractKnowledge(@RequestBody JobSkillExtractionRequest request);

    @PostMapping("/predict_skills")
    JobSkillsResponse extractSkills(@RequestBody JobSkillExtractionRequest request);
}