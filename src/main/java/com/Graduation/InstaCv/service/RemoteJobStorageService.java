package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.RemoteOkJobResponse;
import com.Graduation.InstaCv.data.enums.AnalyzeStatus;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.mappers.impl.jobs.RemoteOkJobResponseMapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RemoteJobStorageService {
    private final RemoteOKJobScrappingService scrappingService;
    private final JobRepository jobRepository;
    private final RemoteOkJobResponseMapper remoteOkJobResponseMapper;
    private final JobService jobService;
    private final ProfileRepository profileRepository;
    private static final Integer lastDaysCount = 10;

    /**
     * Fetches new jobs from RemoteOK API and saves them to the main job table
     *
     * @return The number of new jobs saved
     */
    @Transactional
    @Scheduled(cron = "0 0 */3 * * *")
    public int fetchAndSaveNewJobsFromRemoteOkApi() {
        // Get all jobs from the API
        List<RemoteOkJobResponse> apiJobs = scrappingService.getItJobs(lastDaysCount);

        if (apiJobs.isEmpty()) return 0;

        // Get existing remote job IDs from the database
        Set<String> existingRemoteIds = jobRepository.findAllRemoteIds();

        // Filter out jobs that already exist in the database
        List<RemoteOkJobResponse> newJobs = apiJobs.stream()
                .filter(job -> !existingRemoteIds.contains(job.getId()))
                .toList();

        if (newJobs.isEmpty()) return 0;

        // Convert DTOs to Job entities and save them
        List<Job> jobEntities = new ArrayList<>();
        List<Profile> profiles = profileRepository.findAll();

        // for each remoteJobDto and jobEntity, extract skills and save them
        for (RemoteOkJobResponse remoteJob : newJobs) {
            Job jobEntity = remoteOkJobResponseMapper.toJobEntity(remoteJob);

            boolean skip = false;
            try {
                jobService.jobThroughLLM(jobEntity);
            } catch (Exception e) {
                System.out.println("LLM analysis failed for job: " + remoteJob.getId() + ", skipping skill extraction.");
                skip = true;
            }
            try {
                if (skip || remoteJob != newJobs.get(newJobs.size() - 1)) {
                    Thread.sleep(100);
                    if (skip) Thread.sleep(1000); // Additional delay if skip is true
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (skip) continue;

            try {
                jobEntity = jobService.extractSkills(jobEntity, true, false);
            } catch (Exception e) {
                jobEntity.setSkillExtractionStatus(AnalyzeStatus.FAILED);
            }
            jobEntity.setSkillExtractionStatus(AnalyzeStatus.COMPLETED);
            jobEntities.add(jobEntity);
        }

        jobEntities = jobRepository.saveAll(jobEntities);

        for (Job jobEntity : jobEntities) {
            if (!jobEntity.getSkillExtractionStatus().equals(AnalyzeStatus.FAILED)) {
                for (Profile profile : profiles)
                    jobService.analyzeSkillsMatchingNoSave(jobEntity, profile, false);
            }
        }
        return jobRepository.saveAll(jobEntities).size();
    }

    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void analyzeRecentJobsForMissingProfiles() {
        OffsetDateTime oneWeekAgo = OffsetDateTime.now().minusDays(lastDaysCount);
        List<Job> recentJobs = jobRepository.findRecentRemoteJobs(oneWeekAgo);
        List<Profile> allProfiles = profileRepository.findAll();
        for (Job job : recentJobs) {
            Set<Long> analyzedProfileIds = jobRepository.findProfileIdsAnalyzedForJob(job.getId());
            for (Profile profile : allProfiles) {
                if (!analyzedProfileIds.contains(profile.getId()))
                    jobService.analyzeSkillsMatchingNoSave(job, profile, false);
            }
        }
        jobRepository.saveAll(recentJobs);
    }

    public void analyzeRecentJobsForProfile(Long profileId) {
        OffsetDateTime oneWeekAgo = OffsetDateTime.now().minusDays(lastDaysCount);
        List<Job> recentJobs = jobRepository.findRecentRemoteJobs(oneWeekAgo);
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));

        for (Job job : recentJobs) {
            if (jobService.isAnalysisInvalid(job.getId(), profile.getUser().getId()) || !jobRepository.existsJobSkillMatchingAnalysis(job.getId(), profile.getId())) {
//                if (job.getSkillExtractionStatus() != AnalyzeStatus.COMPLETED || job.getHardSkills().isEmpty())
//                    jobService.extractSkills(job, true, false);
//                jobService.analyzeSkillsMatchingNoSave(job, profile, true);
                jobService.fullAnalyze(job.getId(), profile.getUser().getId(), true, true, false);
            }
        }
//        jobRepository.saveAll(recentJobs);
    }

    public Page<Job> getAllRemoteJobs(Pageable pageable) {
        return jobRepository.findAllRemoteJobsPaginated(pageable);
    }
}