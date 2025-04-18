package com.Graduation.InstaCv.gateways;

import com.Graduation.InstaCv.data.dto.request.MatchingSkillsRequest;
import com.Graduation.InstaCv.data.dto.request.ProjectsMatchingRequest;
import com.Graduation.InstaCv.data.model.ProjectsMatchingAnalysis;
import com.Graduation.InstaCv.data.model.SkillMatchingAnalysis;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "SkillMatchingClient", url = "${skill.matching.url}")
public interface SkillMatchingClient {
    // Note here we are receiving in the analysis model directly, which will contain partial data returned from the gateway
    @PostMapping("/match-skills")
    SkillMatchingAnalysis matchSkills(@RequestBody MatchingSkillsRequest request);
    @PostMapping("/match-projects-skills")
    ProjectsMatchingAnalysis matchProjectsSkills(@RequestBody ProjectsMatchingRequest request);
}