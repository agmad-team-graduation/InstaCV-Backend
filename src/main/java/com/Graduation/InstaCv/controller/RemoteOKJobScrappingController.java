package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.Impl.JobMapper;
import com.Graduation.InstaCv.mappers.Impl.RemoteJobMapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.service.RemoteJobStorageService;
import com.Graduation.InstaCv.service.RemoteOKJobScrappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs/scrape")
@RequiredArgsConstructor
public class RemoteOKJobScrappingController {
    private final RemoteJobStorageService storageService;
    private final JobMapper jobMapper;

    @GetMapping("/all")
    public ResponseEntity<List<JobDto>> getAllJobs() {
        // TODO: Add pagination
        List<Job> jobs = storageService.getAllRemoteJobs();
        List<JobDto> jobDtos = jobs.stream()
                .map(jobMapper::mapTo)
                .toList();
        return ResponseEntity.ok(jobDtos);
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncJobs() {
        int newJobsCount = storageService.fetchAndSaveNewJobs();
        return ResponseEntity.ok("Added " + newJobsCount + " new jobs");
    }
}