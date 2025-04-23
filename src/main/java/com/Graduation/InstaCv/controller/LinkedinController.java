package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.response.AuthLink;
import com.Graduation.InstaCv.data.dto.response.LinkedinAccessTokenResponse;
import com.Graduation.InstaCv.data.model.linkedin.LinkedinProfile;
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import com.Graduation.InstaCv.service.Interfaces.ILinkedinService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/linkedin/test")
public class LinkedinController {
    private final ILinkedinService linkedinService;

    @GetMapping("/authorize")
    public ResponseEntity<AuthLink> authorizeLinkedIn() {
        return ResponseEntity.ok(linkedinService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<LinkedinAccessTokenResponse> callback(@RequestParam String code) {
        return ResponseEntity.ok(linkedinService.getAccessToken(code));
    }

    @GetMapping("/profile")
    public ResponseEntity<LinkedinProfile> profile(@RequestParam String accessToken,
                                                   @RequestParam(defaultValue = "false") boolean forceFetch) {
        return ResponseEntity.ok(linkedinService.getUserProfile(accessToken, forceFetch));
    }
}
