package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.ScrapedJobDto;
import com.Graduation.InstaCv.data.dto.ScrapedJobSimpleDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.mappers.impl.jobs.ScrapedJobMapper;
import com.Graduation.InstaCv.service.JobService;
import com.Graduation.InstaCv.service.ProfileService;
import com.Graduation.InstaCv.service.RemoteJobStorageService;
import com.Graduation.InstaCv.service.ScrapedJobService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs/scrape")
@RequiredArgsConstructor
public class ExternalJobController {
    private final RemoteJobStorageService storageService;
    private final ScrapedJobService scrapedJobService;
    private final JobService jobService;
    private final Mapper<Job, ScrapedJobSimpleDto> simpleScrapedJobMapper;
    private final ScrapedJobMapper scrapedJobMapper;
    private final ProfileService profileService;

    @GetMapping("/all")
    public ResponseEntity<Page<ScrapedJobSimpleDto>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "remoteJobData.date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Job> jobsPage = storageService.getAllRemoteJobs(pageable);

        return ResponseEntity.ok(jobsPage.map(simpleScrapedJobMapper::mapTo));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ScrapedJobDto> getJob(@PathVariable Long jobId) {
        Profile profile = profileService.getProfileByUserId(SecurityUtils.getCurrentUserDetails().getId());
        Job job = jobService.fullAnalyze(jobId, profile.getUser().getId(), true, false);
        return ResponseEntity.ok(scrapedJobMapper.mapTo(job, profile));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<ScrapedJobDto>> getRecommendedJobs() {
        Profile profile = profileService.getProfileByUserId(SecurityUtils.getCurrentUserDetails().getId());
        List<Job> jobs = scrapedJobService.getRecommendedJobs(profile.getId());
        return ResponseEntity.ok(jobs.stream()
                .map(job -> scrapedJobMapper.mapTo(job, profile))
                .toList());
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncJobs() {
        int newJobsCount = storageService.fetchAndSaveNewJobsFromRemoteOkApi();
        return ResponseEntity.ok("Added " + newJobsCount + " new jobs");
    }
}