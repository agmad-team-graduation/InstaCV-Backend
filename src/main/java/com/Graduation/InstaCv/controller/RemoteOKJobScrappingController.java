package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.dto.RemoteOkJobDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.Impl.JobMapper;
import com.Graduation.InstaCv.mappers.Impl.RemoteJobMapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.service.RemoteJobStorageService;
import com.Graduation.InstaCv.service.RemoteOKJobScrappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs/scrape")
@RequiredArgsConstructor
public class RemoteOKJobScrappingController {

    private final RemoteOKJobScrappingService remoteOKJobScrappingService;
    private final RemoteJobStorageService storageService;
    private final JobRepository jobRepository;
    private final RemoteJobMapper remoteJobMapper;
    private final JobMapper jobMapper;

    // Endpoints should expose queries to our database only
//    @GetMapping("/dev-jobs")
//    public ResponseEntity<List<RemoteOkJobDto>> getDevJobs(
//            @RequestParam(required = false) String tech,
//            @RequestParam(required = false, defaultValue = "false") boolean recent) {
//
//        return ResponseEntity.ok(remoteOKJobScrappingService.getFilteredDevJobs(tech, recent));
//    }

    @GetMapping("/all")
    public ResponseEntity<List<JobDto>> getAllJobs() {
        List<Job> jobs = jobRepository.findAllByRemoteJobDataIsNotNull();
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

    // Endpoints should expose queries to our database only
//    @GetMapping("/allRemote")
//    public List<JobDto> getAllRemoteJobs() {
//        List<RemoteOkJobDto> remoteJobs = remoteOKJobScrappingService.getFilteredDevJobs(null, false);
//        List<Job> jobs = remoteJobs.stream()
//                .map(remoteJobMapper::toJobEntity)
//                .toList();
//        return jobs.stream()
//                .map(jobMapper::mapTo)
//                .toList();
//    }
}