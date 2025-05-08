package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.cv.*;
import com.Graduation.InstaCv.data.model.cv.items.CvItem;
import com.Graduation.InstaCv.data.model.cv.items.EducationCv;
import com.Graduation.InstaCv.data.model.cv.items.ExperienceCv;
import com.Graduation.InstaCv.data.model.cv.items.ProjectCv;
import com.Graduation.InstaCv.data.model.cv.sections.*;
import com.Graduation.InstaCv.data.model.cv.skills.UserSkillCv;
import com.Graduation.InstaCv.data.model.jobMatching.projectMatching.MatchedProject;
import com.Graduation.InstaCv.data.model.jobMatching.skillMatching.MatchedSkill;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.*;
import com.Graduation.InstaCv.data.dto.TailoredCvDto;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.repository.ProfileRepository;
import com.Graduation.InstaCv.repository.TailoredCvRepository;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.ICvGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CvGenerationService implements ICvGenerationService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JobRepository jobRepository;
    private final TailoredCvRepository tailoredCvRepository;
    private final JobService jobService;
    private final Mapper<UserSkillCv, UserSkill> userSkillCvMapper;
    private final Mapper<ExperienceCv, Experience> experienceCvMapper;
    private final Mapper<EducationCv, Education> educationCvMapper;
    private final Mapper<ProjectCv, Project> projectCvMapper;


    @Override
//    @Transactional
    public TailoredCv generateCv(Long userId, Long jobId) {
        // Check if CV already exists for this user and job
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Profile profile = user.getProfile();
        if (profile == null) throw new ResourceNotFoundException("User has no profile");

        Optional<TailoredCv> existingCv = tailoredCvRepository.findByIdAndProfileId(profile.getId(), jobId);
        if (existingCv.isPresent()) {
            return existingCv.get();
        }

        Job job = jobRepository.findJobByIdAndProfileId(jobId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));


        if (job.getProfile() != null) {
            if (job.isAnalyzeFailed()) // re-analyze the job if it failed
                job = jobService.fullAnalyze(jobId, userId, false);
            else if (!job.isAnalyzed() || !job.isSkillMatchingAnalyzed() || !job.isProjectMatchingAnalyzed()) // job is being analyzed
                throw new ResourceNotFoundException("Job not analyzed yet with id: " + jobId);
        } else {
            // todo: if not analyzed, analyze it (general job)
        }


        // Start building tailored CV
        TailoredCv tailoredCv = TailoredCv.builder()
                .profile(profile)
                .job(job)
                .personalDetails(profile.getPersonalDetails())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<MatchedSkill> matchedSkills = job.getSkillMatchingAnalysis().getMatchedSkills();
        matchedSkills.sort(Comparator.comparing(MatchedSkill::getSimilarity).reversed());

        List<UserSkill> tailoredSkills = matchedSkills.stream()
                .map(MatchedSkill::getUserSkill)
                .sorted(Comparator.comparing(UserSkill::getLevel).reversed())
                .toList();

        // convert to UserSkillCv
        tailoredCv.setSkillSection(
                SkillSection.builder().items(
                                tailoredSkills.stream().map(userSkillCvMapper::mapFrom).toList()
                        ).sectionTitle("Skills")
                        .build());


        // Sort experiences by date
        List<Experience> tailoredExperience = profile.getExperienceList().stream()
                .sorted(Comparator.comparing(Experience::getStartDate).reversed())
                .toList();

        // Convert to ExperienceCv
        tailoredCv.setExperienceSection(
                ExperienceSection.builder().items(
                                tailoredExperience.stream().map(experienceCvMapper::mapFrom).toList()
                        )
                        .sectionTitle("Experience")
                        .build());

        // Sort education by date
        List<Education> tailoredEducation = profile.getEducationList().stream()
                .sorted(Comparator.comparing(Education::getStartDate).reversed())
                .toList();

        // Convert to EducationCv
        tailoredCv.setEducationSection(
                EducationSection.builder().items(
                                tailoredEducation.stream().map(educationCvMapper::mapFrom).toList()
                        )
                        .sectionTitle("Education")
                        .build());

        // Include relevant projects
        List<Project> tailoredProjects = job.getProjectMatchingAnalysis().getProjectsMatchedWithSkills()
                .stream().sorted(Comparator.comparing(MatchedProject::getMatchedSkillsCount).reversed())
                .map(MatchedProject::getProject).toList();

        // Convert to ProjectCv
        tailoredCv.setProjectSection(
                ProjectSection.builder().items(
                                tailoredProjects.stream().map(projectCvMapper::mapFrom).toList()
                        )
                        .sectionTitle("Projects")
                        .build());

        // Generate summary
        String summary = generateProfileSummary(profile, job);
        tailoredCv.setSummary(summary);

        // Set order index
        setOrderIndex(tailoredCv.getEducationSection().getItems().stream().map(e -> (CvItem) e).toList());
        setOrderIndex(tailoredCv.getExperienceSection().getItems().stream().map(e -> (CvItem) e).toList());
        setOrderIndex(tailoredCv.getProjectSection().getItems().stream().map(e -> (CvItem) e).toList());
        setOrderIndex(tailoredCv.getSkillSection().getItems().stream().map(e -> (CvItem) e).toList());

        // Set order index for sections
        setOrderIndexOfSections(tailoredCv);

        // Save and return
        return tailoredCvRepository.save(tailoredCv);
    }

    @Override
    public TailoredCv getCvByIdAndUserId(Long cvId, Long userId) {
        Long profileId = profileRepository.findProfileIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        return tailoredCvRepository.findByIdAndProfileId(cvId, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found or not accessible for user: " + userId));
    }

    @Override
    public List<TailoredCv> getCvsByUserId(Long userId) {
        Long profileId = profileRepository.findProfileIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        return tailoredCvRepository.findByProfileId(profileId);
    }

    @Override
    public TailoredCv getCvByJobIdAndUserId(Long jobId, Long userId) {
        Long profileId = profileRepository.findProfileIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        boolean jobExists = jobRepository.existsByIdAndProfileId(jobId, profileId);
        if (!jobExists)
            throw new ResourceNotFoundException("Job not found for user: " + userId + " and job: " + jobId);
        return tailoredCvRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found for user: " + userId + " and job: " + jobId));
    }

    private String generateProfileSummary(Profile profile, Job job) {
        // Simple summary generation logic
        String jobTitle = job.getTitle();
        String company = job.getCompany();

        return "Professional with experience in " +
                profile.getUserSkills().stream()
                        .limit(5)
                        .map(UserSkill::getSkill)
                        .collect(Collectors.joining(", ")) +
                " seeking a position as " + jobTitle + " at " + company + ".";
    }

    @Override
    public TailoredCv updateCv(Long cvId, Long userId, TailoredCvDto tailoredCvDto) {
        // Get the CV and validate ownership
        TailoredCv existingCv = getCvByIdAndUserId(cvId, userId);

        // Update basic fields
        if (tailoredCvDto.getPersonalDetails() != null) {
            existingCv.setPersonalDetails(tailoredCvDto.getPersonalDetails());
        }
        if (tailoredCvDto.getSummary() != null) {
            existingCv.setSummary(tailoredCvDto.getSummary());
        }

        // Update education section
        if (tailoredCvDto.getEducationSection() != null) {
            existingCv.setEducationSection(tailoredCvDto.getEducationSection());
            existingCv.getEducationSection().setId(null);
            existingCv.getEducationSection().getItems().forEach(e -> e.setId(null));
            validateOrSetOrderIndex(existingCv.getEducationSection().getItems().stream().map(e -> (CvItem) e).toList());
        }

        // Update experience section
        if (tailoredCvDto.getExperienceSection() != null) {
            existingCv.setExperienceSection(tailoredCvDto.getExperienceSection());
            existingCv.getExperienceSection().setId(null);
            existingCv.getExperienceSection().getItems().forEach(e -> e.setId(null));
            validateOrSetOrderIndex(existingCv.getExperienceSection().getItems().stream().map(e -> (CvItem) e).toList());
        }

        // Update skill section
        if (tailoredCvDto.getSkillSection() != null) {
            existingCv.setSkillSection(tailoredCvDto.getSkillSection());
            existingCv.getSkillSection().setId(null);
            existingCv.getSkillSection().getItems().forEach(e -> e.setId(null));
            validateOrSetOrderIndex(existingCv.getSkillSection().getItems().stream().map(e -> (CvItem) e).toList());
        }

        // Update project section
        if (tailoredCvDto.getProjectSection() != null) {
            existingCv.setProjectSection(tailoredCvDto.getProjectSection());
            existingCv.getProjectSection().setId(null);
            // Set relationships and order index
            existingCv.getProjectSection().getItems().forEach(item -> {
                item.setId(null);
                // Handle project skills
                if (item.getSkills() != null) {
                    item.getSkills().forEach(skill -> skill.setId(null));
                    item.getSkills().forEach(skill -> skill.setProjectCv(item));
                }
            });
            validateOrSetOrderIndex(existingCv.getProjectSection().getItems().stream().map(e -> (CvItem) e).toList());
        }

        // Validate or set order index of sections
        validateOrSetOrderIndexOfSections(existingCv);
        // Set updated timestamp
        existingCv.setUpdatedAt(LocalDateTime.now());

        // Save and return
        return tailoredCvRepository.save(existingCv);
    }

    private void setOrderIndex(List<CvItem> items) {
        for (int i = 0; i < items.size(); i++)
            items.get(i).setOrderIndex(i + 1);
    }

    private void setOrderIndexOfSections(TailoredCv cv) {
        cv.getEducationSection().setOrderIndex(1);
        cv.getExperienceSection().setOrderIndex(2);
        cv.getProjectSection().setOrderIndex(3);
        cv.getSkillSection().setOrderIndex(4);
    }

    /**
     * Validates the order index of items in a section. If any item has an invalid order index (<= 0) or if there are
     * duplicate order indexes, it sets a new order index for all items, depending on the order of the items in the list.
     */
    private void validateOrSetOrderIndex(List<CvItem> items) {
        Set<Integer> orderIndexes = new HashSet<>();
        for (CvItem item : items) {
            if (item.getOrderIndex() == null || item.getOrderIndex() <= 0 || !orderIndexes.add(item.getOrderIndex())) {
                setOrderIndex(items);
                return;
            }
        }
    }

    private void validateOrSetOrderIndexOfSections(TailoredCv cv) {
        Set<Integer> orderIndexes = new HashSet<>();
        orderIndexes.add(cv.getEducationSection().getOrderIndex());
        orderIndexes.add(cv.getExperienceSection().getOrderIndex());
        orderIndexes.add(cv.getProjectSection().getOrderIndex());
        orderIndexes.add(cv.getSkillSection().getOrderIndex());
        Integer minOrderIndex = Collections.min(orderIndexes);
        Integer maxOrderIndex = Collections.max(orderIndexes);
        if (orderIndexes.size() != 4 || orderIndexes.contains(null) || minOrderIndex <= 0 || maxOrderIndex > 4)
            setOrderIndexOfSections(cv);
    }
}