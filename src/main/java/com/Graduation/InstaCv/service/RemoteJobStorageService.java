package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.RemoteOkJobDto;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.Impl.RemoteJobMapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.repository.RemoteJobDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemoteJobStorageService {
    private final RemoteOKJobScrappingService scrappingService;
    private final JobRepository jobRepository;
    private final RemoteJobDataRepository remoteJobDataRepository;
    private final RemoteJobMapper remoteJobMapper;
    private final JobService jobService;
    private static final Integer lastDaysCount = 14;

    /**
     * Fetches new jobs from RemoteOK API and saves them to the main job table
     *
     * @return The number of new jobs saved
     */
    @Transactional
    public int fetchAndSaveNewJobs() {
        // Get all jobs from the API
        List<RemoteOkJobDto> apiJobs = scrappingService.getFilteredDevJobs(lastDaysCount);

        if (apiJobs.isEmpty()) return 0;

        // Get existing remote job IDs from the database
        Set<String> existingRemoteIds = remoteJobDataRepository.findAllRemoteIds();

        // Filter out jobs that already exist in the database
        List<RemoteOkJobDto> newJobs = apiJobs.stream()
                .filter(job -> !existingRemoteIds.contains(job.getId()))
                .toList();

        if (newJobs.isEmpty()) return 0;

        // Convert DTOs to Job entities and save them
        List<Job> jobEntities = new ArrayList<>();

        // for each remoteJobDto and jobEntity, extract skills and save them
        for (RemoteOkJobDto remoteJob : newJobs) {
            Job jobEntity = remoteJobMapper.toJobEntity(remoteJob);
            jobEntity = jobService.extractSkillsRemoteJob(remoteJob, jobEntity);
            jobEntities.add(jobEntity);
        }

        jobEntities = jobRepository.saveAll(jobEntities);



        return newJobs.size();
    }

    /**
     * Scheduled task to automatically fetch new jobs every 2 minutes
     */
//    @Scheduled(fixedRate = 120000 * 30 * 6) // 6 hours in milliseconds
    @Scheduled(fixedRate = 2 * 60 * 1000) // 2 minutes in milliseconds
    @Transactional
    public void scheduledJobSync() {
        int newJobsCount = fetchAndSaveNewJobs();
        System.out.println("Scheduled job sync completed. Added " + newJobsCount + " new jobs.");
    }


    public List<Job> getAllRemoteJobs() {
        return jobRepository.findAllRemoteJobsSortedByDateDesc();
    }
}