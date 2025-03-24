package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/jobs")
public class JobController {
    private final JobService jobService;
//    private final JobMapper jobMapper;

    @PostMapping("/add")
    public ResponseEntity<Job> addJob(@RequestBody Job job) {
        // TODO: Implement this method, to add the job to the database
        // TODO: And replace the direct usage of Job, with DTOs (JobAddRequest, JobResponse), and a Mapper
        Job addedJob = jobService.addJob(Job.builder()
                .title("Software Engineer")
                .description("We are looking for a software engineer to join our team," +
                        " you will be responsible for developing and maintaining high-quality software products." +
                        "Skills: Java, Spring Boot, Angular, React, Node.js, SQL, NoSQL, Docker, Kubernetes, " +
                        "AWS, Azure, GCP, Strong Data Analysis Skills. Soft skills: Teamwork, Communication, Problem-solving, Time management.")
                .company("Google")
                .build());
        return ResponseEntity.ok(addedJob);
    }

    // get job by id
    @GetMapping("/{jobId}")
    public Job getJob(@PathVariable Long jobId) {
        return jobService.getJob(jobId);
    }

    @GetMapping("/analyze/{jobId}")
    public CompletableFuture<ResponseEntity<Job>> analyzeJob(
            @PathVariable Long jobId,
            @RequestParam(name = "force", defaultValue = "false") boolean forceAnalyze) {
        return jobService.analyzeJob(jobId, forceAnalyze).thenApply(ResponseEntity::ok);
    }
}
