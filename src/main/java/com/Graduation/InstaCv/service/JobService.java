package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.response.ExtractedJobSkillResponse;
import com.Graduation.InstaCv.data.dto.response.InterviewQuestionsResponse;
import com.Graduation.InstaCv.data.dto.response.JobKnowledgeResponse;
import com.Graduation.InstaCv.data.dto.response.JobSkillsResponse;
import com.Graduation.InstaCv.data.dto.response.JobllmResponseDTO;
import com.Graduation.InstaCv.data.enums.AnalyzeStatus;
import com.Graduation.InstaCv.data.enums.JobSortField;
import com.Graduation.InstaCv.data.enums.SkillType;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.SkillMatchingAnalysis;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.gateways.GroqChatCompletionClient;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import com.Graduation.InstaCv.utils.JobsPaginationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class JobService implements IJobService {
    private final JobRepository jobRepository;
    private final ProfileService profileService;
    private final JobSkillService jobSkillService;
    private final Mapper<JobSkill, ExtractedJobSkillResponse> jobSkillMapper;
    private final JobsPaginationUtils jobsPaginationUtils;
    private final GroqChatCompletionClient llmClient;

    @Override
    public Job addJob(Job job, Profile profile) {
        job.setId(null);
        job.setProfile(profile);
        job.setAddDate(java.time.OffsetDateTime.now());
//        jobThroughLLM(job);
        return jobRepository.save(job);
    }

    @Override
    public Job fullAnalyze(Long jobId, Long userId, boolean isExternalJob, boolean forceAnalyze, boolean analyzeProjects) {
        Job job = getJob(jobId, userId, isExternalJob);
        jobRepository.save(extractSkills(job, isExternalJob, forceAnalyze));
        job = analyzeSkillsMatching(jobId, userId, isExternalJob, forceAnalyze);
        job = jobRepository.save(job);
        if (analyzeProjects) {
            job = analyzeProjectsMatching(jobId, userId, isExternalJob, forceAnalyze);
            return jobRepository.save(job);
        } else
            return job;
    }

    @Override
    public Job jobThroughLLM(Long jobID) {
        Job job = jobRepository.findById(jobID)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobID));
        return jobThroughLLM(job);
    }

    @Override
    public Job jobThroughLLM(Job job) {
        // Extract the description
        String description = job.getDescription();

        String systemPrompt = """
                You are a specialized assistant that processes noisy job descriptions from Remote OK for optimal skill extraction by NLP models. Your task is to:
                                
                1. REMOVE: Company marketing, excessive branding, application instructions, benefits details, company history, redundant phrases, buzzwords, and non-technical fluff
                2. PRIORITIZE & PRESERVE: Technical skills, programming languages, frameworks, tools, libraries, years of experience, education requirements, specific technologies, development methodologies, and core technical responsibilities
                3. STRUCTURE: Organize the cleaned description to highlight technical requirements clearly and concisely
                4. EXTRACT: Job title (standardized format) and company name if clearly mentioned
                5. SUMMARIZE: Create a focused 2-3 sentence summary emphasizing key technical requirements and role responsibilities
                                
                OPTIMIZATION FOR SKILL EXTRACTION:
                - Group similar technical skills together
                - Use standard technology names (React.js not "React", Node.js not "Node")
                - Include experience levels with technologies when mentioned
                - Preserve specific version numbers or technical specifications
                - Maintain clear separation between required vs preferred skills
                                
                Return ONLY a valid JSON object with these keys:
                - "job_title": string (clean, standardized format like "Full Stack Developer", "DevOps Engineer")
                - "company_name": string or null
                - "summary": string (focus on role type and key technical requirements)
                - "rewritten_description": string (clean, skill-focused, structured for NLP extraction)
                                
                CRITICAL: Response must be ONLY the JSON object. No markdown, explanations, or extra text.
                                
                Example:
                {"job_title": "Full Stack Developer", "company_name": "TechCorp", "summary": "Senior Full Stack Developer position requiring React.js, Node.js, and AWS experience for building scalable web applications. Requires 4+ years experience with modern JavaScript stack.", "rewritten_description": "Full Stack Developer position requiring 4+ years experience. Required technical skills: React.js, Node.js, JavaScript ES6+, HTML5, CSS3, MongoDB, REST APIs, Git. AWS experience required including EC2, S3, Lambda. Responsibilities: Develop responsive web applications, design database schemas, implement REST APIs, code reviews, unit testing. Preferred: TypeScript, Docker, Kubernetes, CI/CD pipelines. Bachelor's degree in Computer Science or equivalent experience required."}
                """;

        String userContent = """
                Please process the following job description:
                %s
                """.formatted(description);

        if (willExceedTokenLimit(systemPrompt, userContent)) {
            throw new IllegalArgumentException("The combined length of the system prompt and user content exceeds the token limit for the LLM. Please shorten the input.");
        }

        // Call the LLM service to process the job description
        String llmResponse = llmClient.chatCompletion(systemPrompt, userContent);

        // Parse the JSON response into DTO
        JobllmResponseDTO jobllmResponseDTO = llmClient.extractAndParseJson(llmResponse, JobllmResponseDTO.class);

        // Update the job entity with the LLM response
        if (jobllmResponseDTO.getJobTitle() != null && !jobllmResponseDTO.getJobTitle().isEmpty())
            job.setTitle(jobllmResponseDTO.getJobTitle());
        if (jobllmResponseDTO.getSummary() != null && !jobllmResponseDTO.getSummary().isEmpty())
            job.setSummary(jobllmResponseDTO.getSummary());
        if (jobllmResponseDTO.getCompanyName() != null && !jobllmResponseDTO.getCompanyName().isEmpty())
            job.setCompany(jobllmResponseDTO.getCompanyName());
        if (jobllmResponseDTO.getRewrittenDescription() != null && !jobllmResponseDTO.getRewrittenDescription().isEmpty())
            job.setDescription(jobllmResponseDTO.getRewrittenDescription());

        return job;
    }

    private boolean willExceedTokenLimit(String systemPrompt, String userContent) {
        String combinedContent = systemPrompt + userContent;
        int wordsCount = combinedContent.split("\\s+").length;
        int tokenCount = (int) Math.ceil(wordsCount / 0.75); // Rough estimate: 1 token ~ 0.75 words
        return false; // return false for now!
    }

    private Job getJob(Long jobId, Long userId, boolean isExternalJob) {
        Profile profile = profileService.getProfileByUserId(userId);
        if (!isExternalJob) {
            return jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId + " for user with id: " + userId));
        } else {
            return jobRepository.findJobByIdAndProfileIsNull(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId + " for external job"));
        }
    }

    public Job extractSkills(Job job, boolean isExternal, boolean forceAnalyze) {
        if (job.getSkillExtractionStatus() == AnalyzeStatus.COMPLETED && !forceAnalyze) return job;
        String description = isExternal ? job.getRemoteJobData().getModifiedDescription() : job.getDescription();

        CompletableFuture<JobKnowledgeResponse> knowledgePredictionsFuture = jobSkillService.extractKnowledge(description);
        CompletableFuture<JobSkillsResponse> skillsPredictionsFuture = jobSkillService.extractSkills(description);

        CompletableFuture.allOf(knowledgePredictionsFuture, skillsPredictionsFuture).join();

        return updateJobWithAnalysis(job, knowledgePredictionsFuture.join(), skillsPredictionsFuture.join());
    }

    // TODO: Can the same problem of persistent (that i created analyzeSkillsMatchingWithSave to fix) happen here when we use
    // analyzeSkillsMatchingNoSave no the external jobs? probably not because there is no half analysis for them in db?
    // TODO: Just see how to handle with external jobs, I don't like this function, make it safe even if extractSkills returned newthings
    public Job analyzeSkillsMatchingNoSave(Job job, Profile profile, boolean forceAnalyze) {
        if (!forceAnalyze && jobRepository.existsJobSkillMatchingAnalysis(job.getId(), profile.getId())) return job;
        job.getSkillMatchingAnalyses().removeIf(analysis -> analysis.getProfile().equals(profile));
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

    public Page<Job> getJobsByUserId(Long userId, Pageable pageable) {
        Profile profile = profileService.getProfileByUserId(userId);
        return jobRepository.findJobsByProfileId(profile.getId(), pageable);
    }

    @Override
    public Page<Job> getRecommendedExternalJobsPaginated(Long profileId, Pageable pageable, JobSortField sortField) {
        if (sortField.isCustomSort()) {
            List<Job> jobs = jobRepository.findAnalyzedScrapedJobsByProfileId(profileId);

            List<Job> sorted = jobs.stream()
                    .sorted(Comparator.comparingDouble(job ->
                            ((Job) job).getSkillMatchingAnalyses().stream()
                                    .filter(a -> a.getProfile().getId().equals(profileId))
                                    .findFirst()
                                    .map(SkillMatchingAnalysis::getMatchedSkillsPercentage)
                                    .orElse(0f)
                    ).reversed()) // DESC by default
                    .toList();

            if (pageable.getSort().getOrderFor("MATCH_SCORE").getDirection().isAscending()) {
                sorted = new ArrayList<>(sorted);
                Collections.reverse(sorted);
            }

            // filter by non-null and non-zero match score
            List<Job> nonZeroFiltered = sorted.stream()
                    .filter(job -> job.getSkillMatchingAnalyses().stream()
                            .filter(a -> a.getProfile().getId().equals(profileId))
                            .findFirst()
                            .map(SkillMatchingAnalysis::getMatchedSkillsPercentage)
                            .orElse(0f) > 0)
                    .toList();

            return jobsPaginationUtils.createPageFromList(nonZeroFiltered, pageable);
        } else {
            return jobRepository.findAnalyzedScrapedJobsByProfileIdPaginated(profileId, pageable);
        }
    }


    // TODO: Move to another service?
    private Job analyzeSkillsMatching(Long jobId, Long userId, boolean isExternal, boolean forceAnalyze) {
        Profile profile = profileService.getProfileByUserId(userId);
        Job job = getJob(jobId, userId, isExternal);
        return jobRepository.save(analyzeSkillsMatchingNoSave(job, profile, forceAnalyze));
    }

    private Job analyzeProjectsMatching(Long jobId, Long userId, boolean isExternal, boolean forceAnalyze) {
        Profile profile = profileService.getProfileByUserId(userId);
        Job job = getJob(jobId, userId, isExternal);
        if (!forceAnalyze && jobRepository.existsJobProjectMatchingAnalysis(job.getId(), profile.getId())) return job;
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
        job.getProjectMatchingAnalyses().clear();
        job.getSkillMatchingAnalyses().clear();
        job.getJobSkills().clear();

        job.getJobSkills().addAll(hardSkills);
        job.getJobSkills().addAll(softSkills);

        job.getJobSkills().forEach(jobSkill -> jobSkill.setJob(job));
        job.setSkillExtractionStatus(AnalyzeStatus.COMPLETED);

        return job;
    }

    public InterviewQuestionsResponse generateInterviewQuestions(Long jobId, Integer numberOfQuestions, Long userId) {
        // Get the job and verify ownership
        Job job = getJobByIdAndUserId(jobId, userId);

        String systemPrompt = """
                You are an expert HR professional and technical interviewer. Your task is to generate relevant interview questions for a specific job based on its description.
                                
                Your goal is to create diverse, practical interview questions that would help assess a candidate's suitability for the role.
                                
                Categories to consider:
                - Technical Skills: Questions about specific technologies, programming languages, tools mentioned in the job
                - Problem Solving: Scenario-based questions and coding challenges
                - Experience: Questions about past projects and experiences
                - Soft Skills: Communication, teamwork, leadership questions
                - Company Culture: Questions about work style and cultural fit
                                
                Difficulty levels:
                - Easy: Basic knowledge and understanding
                - Medium: Practical application and experience
                - Hard: Advanced concepts and complex problem-solving
                                
                For the expectedAnswer field: Provide the direct answer content WITHOUT prefacing phrases like "The answer should be", "The candidate should say", "A good answer would be", etc. Just provide the actual answer content directly.
                                
                Return ONLY a valid JSON object with the following structure:
                {
                  "questions": [
                    {
                      "question": "string",
                      "category": "string",
                      "difficulty": "string",
                      "expectedAnswer": "string (direct answer content only)"
                    }
                  ]
                }
                                
                CRITICAL: Your response must be ONLY the JSON object. No reasoning, thinking, explanation, or extra text. No markdown formatting or code blocks.
                """;

        String userContent = """
                Generate exactly %d interview questions for the following job:
                                
                Job Title: %s
                Company: %s
                Job Description: %s
                                
                Make sure to create a good mix of technical and soft skill questions with varying difficulty levels.
                """.formatted(numberOfQuestions,
                job.getTitle() != null ? job.getTitle() : "Not specified",
                job.getCompany() != null ? job.getCompany() : "Not specified",
                job.getDescription());

        // Call the LLM service to generate interview questions
        String llmResponse = llmClient.chatCompletion(systemPrompt, userContent);

        // Parse the JSON response
        InterviewQuestionsResponse response = llmClient.extractAndParseJson(llmResponse, InterviewQuestionsResponse.class);

        // Set additional job information
        response.setJobId(job.getId());
        response.setJobTitle(job.getTitle());
        response.setCompany(job.getCompany());

        return response;
    }
}