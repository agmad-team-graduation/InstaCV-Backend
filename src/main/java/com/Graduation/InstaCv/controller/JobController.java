package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.dto.JobSimpleDto;
import com.Graduation.InstaCv.data.dto.request.InterviewQuestionsRequest;
import com.Graduation.InstaCv.data.dto.response.InterviewQuestionsResponse;
import com.Graduation.InstaCv.data.dto.response.PaginatedResponse;
import com.Graduation.InstaCv.data.enums.JobSortField;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.gateways.GroqChatCompletionClient;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.Graduation.InstaCv.utils.JobsPaginationUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/jobs")
public class JobController {
    private final IJobService jobService;
    private final IProfileService profileService;
    private final ContextAwareMapper<Job, JobDto, Profile> jobMapper;
    private final ContextAwareMapper<Job, JobSimpleDto, Profile> jobSimpleMapper;
    private final GroqChatCompletionClient llmClient;

    @GetMapping("/all")
    public ResponseEntity<PaginatedResponse<JobDto>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DATE") JobSortField sortField,
            @RequestParam(defaultValue = "desc") String direction) {
        Pageable pageable = JobsPaginationUtils.buildPageable(page, size, sortField, direction);
        Long userId = SecurityUtils.getCurrentUserDetails().getId();

        Page<Job> jobsPage = jobService.getJobsByUserId(userId, pageable);
        Page<JobDto> dtoPage = jobsPage.map(jobMapper::mapTo);

        return ResponseEntity.ok(new PaginatedResponse<>(dtoPage));
    }

    @PostMapping("/add")
    public ResponseEntity<JobDto> addJob(@RequestBody JobSimpleDto job) {
        Profile profile = profileService.getProfileByUserId(SecurityUtils.getCurrentUserDetails().getId());
        Job savedJob = jobService.addJob(jobSimpleMapper.mapFrom(job, profile), profile);
        return new ResponseEntity<>(jobMapper.mapTo(savedJob), HttpStatus.CREATED);
    }

    @GetMapping("/{jobId}")
    public JobDto getJob(@PathVariable Long jobId, @RequestParam(defaultValue = "false") boolean forceAnalyze) {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Job jobFound = jobService.getJobByIdAndUserId(jobId, userId);
        if (jobFound.getSkillMatchingAnalyses().isEmpty() || forceAnalyze)
            jobFound = jobService.fullAnalyze(jobId, userId, false, forceAnalyze, false);
        return jobMapper.mapTo(jobFound);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        jobService.deleteJobByIdAndUserId(jobId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{jobId}/llm")
    public ResponseEntity<Job> analyzeJobWithLLM(@PathVariable Long jobId) {
        Job job = jobService.jobThroughLLM(jobId);

        return ResponseEntity.ok(job);
    }

    @PostMapping("/interview-questions")
    public ResponseEntity<InterviewQuestionsResponse> generateInterviewQuestions(
            @Valid @RequestBody InterviewQuestionsRequest request) {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        InterviewQuestionsResponse response = jobService.generateInterviewQuestions(
                request.getJobId(),
                request.getNumberOfQuestions(),
                userId
        );
        return ResponseEntity.ok(response);
    }

}
