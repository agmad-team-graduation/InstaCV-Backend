package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.DevJobs;
import com.Graduation.InstaCv.service.DevJobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/jobs")
public class DevJobsController {

    private final DevJobsService devJobsService;

    @Autowired
    public DevJobsController(DevJobsService devJobsService) {
        this.devJobsService = devJobsService;
    }

    @GetMapping("/dev-jobs")
    public ResponseEntity<List<DevJobs>> getDevJobs(
            @RequestParam(required = false) String tech,
            @RequestParam(required = false, defaultValue = "false") boolean recent) {

        // Delegate all parameter handling to the service
        return ResponseEntity.ok(devJobsService.getFilteredDevJobs(tech, recent));
    }


}