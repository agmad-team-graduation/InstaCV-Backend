package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.service.JobService;
import com.Graduation.InstaCv.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/jobs")
public class JobController {
    private final JobService jobService;
    private final ProfileService profileService;
    private final Mapper<Job, JobDto> jobMapper;

    @PostMapping("/add")
    public ResponseEntity<JobDto> addJob(@RequestBody JobDto job) {
        Job jobE = jobMapper.mapFrom(job);
        Profile profile = profileService.getProfile(job.getProfileId()).getProfile();
        Job SavedJob = jobService.addJob(jobE, profile);
        return new ResponseEntity<>(jobMapper.mapTo(SavedJob), HttpStatus.CREATED);
    }

    @GetMapping("/jobs")
    public List<JobDto> getAllJobs() {
        List<Job> jobsEntity = jobService.getJobs();
        return jobsEntity.stream().map(jobMapper::mapTo).collect(Collectors.toList());
    }

    // get job by id
    @GetMapping("/{jobId}")
    public ResponseEntity<JobDto> getJob(@PathVariable Long jobId) {
        Job jobFound = jobService.getJob(jobId);
        return new ResponseEntity<>(jobMapper.mapTo(jobFound), HttpStatus.OK);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> DeleteJob(@PathVariable Long jobId) {
        jobService.delete(jobId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/analyze/{jobId}")
    public CompletableFuture<ResponseEntity<Job>> analyzeJob(
            @PathVariable Long jobId,
            @RequestParam(name = "force", defaultValue = "false") boolean forceAnalyze) {
        return jobService.analyzeJob(jobId, forceAnalyze).thenApply(ResponseEntity::ok);
    }
}
