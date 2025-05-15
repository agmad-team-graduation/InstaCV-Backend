package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.response.ExtractedJobSkillResponse;
import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.data.enums.AnalyzeStatus;
import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class JobService implements IJobService {
    private final JobRepository jobRepository;
    private final ProfileService profileService;
    private final JobSkillService jobSkillService;
    private final Mapper<JobSkill, ExtractedJobSkillResponse> jobSkillMapper;

    @Override
    public Job addJob(Job job, Profile profile) {
        job.setId(null);
        job.setProfile(profile);
        return jobRepository.save(job);
    }

    @Override
    public Job fullAnalyze(Long jobId, Long userId, boolean isExternalJob, boolean forceAnalyze) {
        Job job = analyzeSkillsMatching(jobId, userId, isExternalJob, forceAnalyze);
        jobRepository.save(job);
        job = analyzeProjectsMatching(jobId, userId, isExternalJob, forceAnalyze);
        job.setCompleteAnalysisStatus(AnalyzeStatus.COMPLETED);
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    @Async
    public void backgroundFullAnalyzeJob(Long jobId, Long userId, Boolean isExternalJob, Boolean forceAnalyze) {
        Job job = getJobByIdAndUserId(jobId, userId);
        try {
            job.setCompleteAnalysisStatus(AnalyzeStatus.IN_PROGRESS);
            jobRepository.save(job);
            jobRepository.flush();
            // No need to call skillExtraction (will be done internally)
            job = analyzeSkillsMatching(jobId, userId, isExternalJob, forceAnalyze);
            jobRepository.save(job);
            job = analyzeProjectsMatching(jobId, userId, isExternalJob, forceAnalyze);
            job.setCompleteAnalysisStatus(AnalyzeStatus.COMPLETED);
            jobRepository.save(job);
        } catch (Exception e) {
            job.setCompleteAnalysisStatus(AnalyzeStatus.FAILED);
            jobRepository.save(job);
        }
    }

    public Job extractSkills(Job job, boolean isExternal, boolean forceAnalyze) {
        if (isExternal && job.getSkillExtractionStatus() == AnalyzeStatus.COMPLETED && !forceAnalyze) return job;
        if (!isExternal && job.getCompleteAnalysisStatus() == AnalyzeStatus.COMPLETED && !forceAnalyze) return job;
        if (!job.getJobSkills().isEmpty() && !forceAnalyze) return job;

        String description = isExternal ? job.getRemoteJobData().getModifiedDescription() : job.getDescription();

        CompletableFuture<JobKnowledgeResponse> knowledgePredictionsFuture = jobSkillService.extractKnowledge(description);
        CompletableFuture<JobSkillsResponse> skillsPredictionsFuture = jobSkillService.extractSkills(description);

        CompletableFuture.allOf(knowledgePredictionsFuture, skillsPredictionsFuture).join();

        return updateJobWithAnalysis(job, knowledgePredictionsFuture.join(), skillsPredictionsFuture.join());
    }

    // TODO: Can the same problem of persistent (that i created analyzeSkillsMatchingWithSave to fix) happen here when we use
    // analyzeSkillsMatchingNoSave no the external jobs? probably not because there is no half analysis for them in db?
    // TODO: Just see how to handle with external jobs, I don't like this function, make it safe even if extractSkills returned newthings
    public Job analyzeSkillsMatchingNoSave(Job job, Profile profile, boolean isExternal, boolean forceAnalyze) {
        if (!forceAnalyze && jobRepository.existsJobSkillMatchingAnalysis(job.getId(), profile.getId())) return job;
        job = extractSkills(job, isExternal, forceAnalyze);
        job.getSkillMatchingAnalyses().add(jobSkillService.analyzeSkillsMatching(job, profile.getUser()));
        job.getSkillMatchingAnalyses().getLast().setJob(job);
        job.getSkillMatchingAnalyses().getLast().setProfile(profile);
        return job;
    }


    public Job analyzeSkillsMatchingWithSave(Job job, Profile profile, boolean isExternal, boolean forceAnalyze) {
        if (!forceAnalyze && jobRepository.existsJobSkillMatchingAnalysis(job.getId(), profile.getId())) return job;
        job = extractSkills(job, isExternal, forceAnalyze);
        job = jobRepository.save(job);
        job.getSkillMatchingAnalyses().add(jobSkillService.analyzeSkillsMatching(job, profile.getUser()));
        job.getSkillMatchingAnalyses().getLast().setJob(job);
        job.getSkillMatchingAnalyses().getLast().setProfile(profile);
        return job;
    }

    @Override
    public Job getJobByIdAndUserId(Long jobId, Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);
        return jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId + " for user with id: " + userId));
    }

    @Override
    public void deleteJobByIdAndUserId(Long jobId, Long userId) {
        Job job = getJobByIdAndUserId(jobId, userId); // ensures ownership
        jobRepository.delete(job);
    }

    @Override
    public List<Job> getJobsByUserId(Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);
        return jobRepository.findJobsByProfileId(profile.getId());
    }

    // TODO: Move to another service?
    private Job analyzeSkillsMatching(Long jobId, Long userId, boolean isExternal, boolean forceAnalyze) {
        Profile profile = profileService.getProfileByUserId(userId);
        Job job;
        if (!isExternal)
            job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        else
            job = jobRepository.findJobByIdAndProfileIsNull(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        if (!isExternal)
            return jobRepository.save(analyzeSkillsMatchingWithSave(job, profile, isExternal, forceAnalyze));
        return jobRepository.save(analyzeSkillsMatchingNoSave(job, profile, isExternal, forceAnalyze));
    }

    private Job analyzeProjectsMatching(Long jobId, Long userId, boolean isExternal, boolean forceAnalyze) {
        Profile profile = profileService.getProfileByUserId(userId);
        Job job;
        if (!isExternal)
            job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        else
            job = jobRepository.findJobByIdAndProfileIsNull(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        if (!forceAnalyze && jobRepository.existsJobProjectMatchingAnalysis(job.getId(), profile.getId())) return job;
        job = extractSkills(job, isExternal, forceAnalyze);
        job.getProjectMatchingAnalyses().add(jobSkillService.analyzeProjectsMatching(job, profile.getUser()));
        job.getProjectMatchingAnalyses().getLast().setJob(job);
        job.getProjectMatchingAnalyses().getLast().setProfile(profile);
        return jobRepository.save(job);
    }

    private Job updateJobWithAnalysis(Job job, JobKnowledgeResponse knowledge, JobSkillsResponse skills) {
        List<JobSkill> hardSkills = knowledge.getKnowledgePredictions().stream()
                .map(jobSkillMapper::mapFrom)
                .toList();
        hardSkills.forEach(jobSkill -> jobSkill.setSkillType(SkillType.HARD));

        List<JobSkill> softSkills = skills.getSkillsPredictions().stream()
                .map(jobSkillMapper::mapFrom)
                .toList();
        softSkills.forEach(jobSkill -> jobSkill.setSkillType(SkillType.SOFT));

        // Clear and add new skills, instead of directly setting to avoid orphan removal error
        job.getJobSkills().clear();
        job.getJobSkills().addAll(hardSkills);
        job.getJobSkills().addAll(softSkills);

        job.getJobSkills().forEach(jobSkill -> jobSkill.setJob(job));

        return job;
    }
}