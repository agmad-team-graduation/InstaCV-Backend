package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.model.Job;
import com.Graduation.InstaCv.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    @PostMapping("/add")
    public ResponseEntity<Job> addJob(@RequestBody Job job) {
        // TODO: Implement this method, to add the job to the database
        // TODO: And replace the direct usage of Job, with DTOs (JobAddRequest, JobResponse), and a Mapper
        Job addedJob = jobService.addJob(Job.builder()
                .title("Software Engineer")
                .description("We are looking for a software engineer to join our team," +
                        " you will be responsible for developing and maintaining high-quality software products." +
                        "Skills: Java, Spring Boot, Angular, React, Node.js, SQL, NoSQL, Docker, Kubernetes, " +
                        "AWS, Azure, GCP")
                .company("Google")
                .build());
        return ResponseEntity.ok(addedJob);
    }

    @GetMapping("/analyze/{jobId}")
    public ResponseEntity<String> analyzeJob(@PathVariable Long jobId) {
        // TODO: Implement this method, to analyze the job description
        return ResponseEntity.ok("Job analyzed successfully");
    }
}
