package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.RemoteOkJobDto;
import com.Graduation.InstaCv.service.RemoteJobStorageService;
import com.Graduation.InstaCv.service.RemoteOKJobScrappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
public class RemoteOKJobScrappingController {

    private final RemoteOKJobScrappingService remoteOKJobScrappingService;
    private final RemoteJobStorageService storageService;

    @Autowired
    public RemoteOKJobScrappingController(RemoteOKJobScrappingService remoteOKJobScrappingService, RemoteJobStorageService storageService) {
        this.remoteOKJobScrappingService = remoteOKJobScrappingService;
        this.storageService = storageService;
    }

    @GetMapping("/dev-jobs")
    public ResponseEntity<List<RemoteOkJobDto>> getDevJobs(
            @RequestParam(required = false) String tech,
            @RequestParam(required = false, defaultValue = "false") boolean recent) {

        return ResponseEntity.ok(remoteOKJobScrappingService.getFilteredDevJobs(tech, recent));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncJobs() {
        int newJobsCount = storageService.fetchAndSaveNewJobs();
        return ResponseEntity.ok("Added " + newJobsCount + " new jobs");
    }

}