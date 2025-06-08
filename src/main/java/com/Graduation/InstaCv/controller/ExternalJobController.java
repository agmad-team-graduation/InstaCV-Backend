package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.ScrapedJobDto;
import com.Graduation.InstaCv.data.dto.ScrapedJobSimpleDto;
import com.Graduation.InstaCv.data.dto.response.PaginatedResponse;
import com.Graduation.InstaCv.data.enums.JobSortField;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.mappers.impl.jobs.ScrapedJobMapper;
import com.Graduation.InstaCv.service.JobService;
import com.Graduation.InstaCv.service.ProfileService;
import com.Graduation.InstaCv.service.RemoteJobStorageService;
import com.Graduation.InstaCv.utils.JobsPaginationUtils;
import com.Graduation.InstaCv.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs/scrape")
@RequiredArgsConstructor
public class ExternalJobController {
    private final RemoteJobStorageService storageService;
    private final JobService jobService;
    private final Mapper<Job, ScrapedJobSimpleDto> simpleScrapedJobMapper;
    private final ScrapedJobMapper scrapedJobMapper;
    private final ProfileService profileService;
    private final JobsPaginationUtils jobsPaginationUtils;

    @GetMapping("/all")
    public ResponseEntity<PaginatedResponse<ScrapedJobSimpleDto>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DATE") JobSortField sortField,
            @RequestParam(defaultValue = "desc") String direction) {
        Pageable pageable = jobsPaginationUtils.buildPageable(page, size, sortField, direction);
        Page<Job> jobsPage = storageService.getAllRemoteJobs(pageable);
        Page<ScrapedJobSimpleDto> dtoPage = jobsPage.map(simpleScrapedJobMapper::mapTo);
        return ResponseEntity.ok(new PaginatedResponse<>(dtoPage));
    }


    @GetMapping("/{jobId}")
    public ResponseEntity<ScrapedJobDto> getJob(@PathVariable Long jobId) {
        Profile profile = profileService.getProfileByUserId(SecurityUtils.getCurrentUserDetails().getId());
        Job job = jobService.fullAnalyze(jobId, profile.getUser().getId(), true, false, false);
        return ResponseEntity.ok(scrapedJobMapper.mapTo(job, profile));
    }

    @PostMapping("/analyze-recommendations")
    public ResponseEntity<PaginatedResponse<ScrapedJobDto>> analyzeRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "MATCH_SCORE") JobSortField sortField,
            @RequestParam(defaultValue = "desc") String direction) {
        Long profileId = profileService.getProfileIdByUserId(SecurityUtils.getCurrentUserDetails().getId());
        storageService.analyzeRecentJobsForProfile(profileId);
        return getRecommendedJobs(page, size, sortField, direction);
    }


    @GetMapping("/get-recommendations")
    public ResponseEntity<PaginatedResponse<ScrapedJobDto>> getRecommendedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "MATCH_SCORE") JobSortField sortField,
            @RequestParam(defaultValue = "desc") String direction) {
        Profile profile = profileService.getProfileByUserId(SecurityUtils.getCurrentUserDetails().getId());
        Pageable pageable = jobsPaginationUtils.buildPageable(page, size, sortField, direction);
        Page<Job> jobPage = jobService.getRecommendedExternalJobsPaginated(profile.getId(), pageable, sortField);
        Page<ScrapedJobDto> dtoPage = jobPage.map(job -> scrapedJobMapper.mapTo(job, profile));
        return ResponseEntity.ok(new PaginatedResponse<>(dtoPage));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncJobs() {
        int newJobsCount = storageService.fetchAndSaveNewJobsFromRemoteOkApi();
        return ResponseEntity.ok("Added " + newJobsCount + " new jobs");
    }
}