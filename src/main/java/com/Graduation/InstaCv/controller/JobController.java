package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.dto.JobSimpleDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import com.Graduation.InstaCv.utils.SecurityUtils;
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
    private final IJobService jobService;
    private final IProfileService profileService;
    private final ContextAwareMapper<Job, JobDto, Profile> jobMapper;
    private final ContextAwareMapper<Job, JobSimpleDto, Profile> jobSimpleMapper;

    @PostMapping("/add")
    public ResponseEntity<JobDto> addJob(@RequestBody JobSimpleDto job) {
        Profile profile = profileService.getProfileByUserId(SecurityUtils.getCurrentUserDetails().getId());
        Job SavedJob = jobService.addJob(jobSimpleMapper.mapFrom(job, profile), profile);
        return new ResponseEntity<>(jobMapper.mapTo(SavedJob), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<JobSimpleDto> getAllJobs() {
        List<Job> jobsEntity = jobService.getJobsByUserId(SecurityUtils.getCurrentUserDetails().getId());
        return jobsEntity.stream().map(jobSimpleMapper::mapTo).collect(Collectors.toList());
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobDto> getJob(@PathVariable Long jobId) {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Job jobFound = jobService.getJobByIdAndUserId(jobId, userId);
        return new ResponseEntity<>(jobMapper.mapTo(jobFound), HttpStatus.OK);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        jobService.deleteJobByIdAndUserId(jobId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/analyze/{jobId}")
    public CompletableFuture<ResponseEntity<JobDto>> analyzeJob(
            @PathVariable Long jobId,
            @RequestParam(name = "force", defaultValue = "false") boolean forceAnalyze) {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        return jobService.analyzeJob(jobId, userId, forceAnalyze)
                .thenApply(job -> new ResponseEntity<>(jobMapper.mapTo(job), HttpStatus.OK));
    }

    @GetMapping("/skill-matching/{jobId}")
    public JobDto getSkillMatching(
            @PathVariable Long jobId,
            @RequestParam(name = "force", defaultValue = "false") boolean forceAnalyze) {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Job job = jobService.analyzeJobMatching(jobId, userId, forceAnalyze);
        return jobMapper.mapTo(job);
    }
}
