package com.Graduation.InstaCv.gateways.linkedin;


import com.Graduation.InstaCv.data.dto.response.linkedin.LinkedinEducationResponse;
import com.Graduation.InstaCv.data.dto.response.linkedin.LinkedinExperienceResponse;
import com.Graduation.InstaCv.data.dto.response.linkedin.LinkedinProfileResponse;
import com.Graduation.InstaCv.data.dto.response.linkedin.LinkedinSkillResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "linkedInApiClient", url = "https://api.linkedin.com/v2")
public interface LinkedinApiClient {
    @GetMapping("/me")
    LinkedinProfileResponse getProfile(@RequestHeader("Authorization") String bearer);

    @GetMapping("/skills")
    List<LinkedinSkillResponse> getSkills(@RequestHeader("Authorization") String bearer);

    @GetMapping("/educations")
    List<LinkedinEducationResponse> getEducations(@RequestHeader("Authorization") String bearer);

    @GetMapping("/positions")
    List<LinkedinExperienceResponse> getExperiences(@RequestHeader("Authorization") String bearer);
}
