package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.RemoteOkJobDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.Impl.RemoteJobMapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.repository.RemoteJobDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RemoteJobStorageService {

    private final RemoteOKJobScrappingService scrappingService;
    private final JobRepository jobRepository;
    private final RemoteJobDataRepository remoteJobDataRepository;
    private final RemoteJobMapper remoteJobMapper;

    @Autowired
    public RemoteJobStorageService(
            RemoteOKJobScrappingService scrappingService,
            JobRepository jobRepository,
            RemoteJobDataRepository remoteJobDataRepository,
            RemoteJobMapper remoteJobMapper) {
        this.scrappingService = scrappingService;
        this.jobRepository = jobRepository;
        this.remoteJobDataRepository = remoteJobDataRepository;
        this.remoteJobMapper = remoteJobMapper;
    }

    /**
     * Fetches new jobs from RemoteOK API and saves them to the main job table
     * @return The number of new jobs saved
     */
    @Transactional
    public int fetchAndSaveNewJobs() {
        // Get all jobs from the API
        List<RemoteOkJobDto> apiJobs = scrappingService.getFilteredDevJobs(null, true);

        if (apiJobs.isEmpty()) {
            return 0;
        }

        // Get existing remote job IDs from the database
        Set<String> existingRemoteIds = remoteJobDataRepository.findAllRemoteIds();

        // Filter out jobs that already exist in the database
        List<RemoteOkJobDto> newJobs = apiJobs.stream()
                .filter(job -> !existingRemoteIds.contains(job.getId()))
                .collect(Collectors.toList());

        if (newJobs.isEmpty()) {
            return 0;
        }

        // Convert DTOs to Job entities and save them
        List<Job> jobEntities = newJobs.stream()
                .map(remoteJobMapper::toJobEntity)
                .collect(Collectors.toList());

        jobRepository.saveAll(jobEntities);

        return newJobs.size();
    }

    /**
     * Scheduled task to automatically fetch new jobs every 2 minutes
     */
    @Scheduled(fixedRate = 120000*30*6) //6 hours in milliseconds
    public void scheduledJobSync() {
        int newJobsCount = fetchAndSaveNewJobs();
        System.out.println("Scheduled job sync completed. Added " + newJobsCount + " new jobs.");
    }

    /**
     * Get all remote jobs from the database
     */
    public List<RemoteOkJobDto> getAllRemoteJobs() {
        return jobRepository.findAllRemoteJobsSortedByDateDesc().stream()
                .map(remoteJobMapper::toRemoteOkJobDto)
                .collect(Collectors.toList());
    }
}