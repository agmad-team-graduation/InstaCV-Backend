package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.StatDTO;
import com.Graduation.InstaCv.service.DashboardService;
import com.Graduation.InstaCv.data.dto.DashboardStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/total-cvs")
    public ResponseEntity<StatDTO> getTotalCvs() {
        StatDTO cvStats = dashboardService.getTotalCvStats();
        return ResponseEntity.ok(cvStats);
    }

    @GetMapping("/saved-jobs")
    public ResponseEntity<StatDTO> getSavedJobs() {
        StatDTO jobStats = dashboardService.getSavedJobsStats();
        return ResponseEntity.ok(jobStats);
    }
}