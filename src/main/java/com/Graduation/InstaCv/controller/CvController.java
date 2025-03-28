package com.Graduation.InstaCv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/cv")
public class CvController {

    @GetMapping("/generate")
    public CvDto generateCv(@RequestParam String userId, @RequestParam(required = false) String jobId) {
        return cvService.generateCv(userId, jobId);
    }

}
