package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.repository.TailoredCvRepository;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.data.dto.DashboardStatsDTO;
import com.Graduation.InstaCv.data.dto.StatDTO;
import com.Graduation.InstaCv.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private TailoredCvRepository tailoredCvRepository;

    @Autowired
    private JobRepository jobRepository;

    public DashboardStatsDTO getDashboardStats() {

        StatDTO cvStats = getTotalCvStats();
        StatDTO jobStats = getSavedJobsStats();

        List<StatDTO> stats = Arrays.asList(cvStats, jobStats);
        return new DashboardStatsDTO(stats);
    }

    public StatDTO getTotalCvStats() {
        // Get total CVs count
        Long UserId = SecurityUtils.getCurrentUserDetails().getId();
        long totalCvs = tailoredCvRepository.countByUserId(UserId);

        // Calculate monthly change
        String change = calculateCvChange(UserId);

        return new StatDTO("Total CVs", String.valueOf(totalCvs), change);
    }

    public StatDTO getSavedJobsStats() {
        Long UserId = SecurityUtils.getCurrentUserDetails().getId();
        // Get saved jobs count
        long savedJobs = jobRepository.countJobsByUserId(UserId);

        // Calculate weekly change
        String change = calculateJobChange(UserId);

        return new StatDTO("Saved Jobs", String.valueOf(savedJobs), change);
    }

    private String calculateCvChange(Long userId) {
        try {
            // Calculate change for the last month from now
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
            long recentCvs = tailoredCvRepository.countCvsCreatedAfterByUserId(userId, oneMonthAgo);

            if (recentCvs > 0) {
                return "+" + recentCvs + " this month";
            } else if (recentCvs == 0) {
                return "No change this month";
            } else {
                return "No change";
            }
        } catch (Exception e) {
            return "No change";
        }
    }

    private String calculateJobChange(Long userId) {
        try {
            // Calculate change for the last month from now
            OffsetDateTime oneMonthAgo = OffsetDateTime.now().minusMonths(1);
            long recentJobs = jobRepository.countJobsAddedAfterByUserId(userId, oneMonthAgo);

            if (recentJobs > 0) {
                return "+" + recentJobs + " this month";
            } else if (recentJobs == 0) {
                return "No change this month";
            } else {
                return "No change";
            }
        } catch (Exception e) {
            return "No change";
        }
    }
}