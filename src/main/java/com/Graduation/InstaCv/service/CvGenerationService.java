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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public TailoredCv generateCv(Long userId, Long jobId) {
        // Check if CV already exists for this user and job
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Profile profile = user.getProfile();
        if (profile == null) throw new ResourceNotFoundException("User has no profile");

        Optional<TailoredCv> existingCv = tailoredCvRepository.findByJobIdAndProfileId(jobId, profile.getId());
        if (existingCv.isPresent()) {
            return existingCv.get();
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        if (job.getProfile() != null && !job.getProfile().getId().equals(profile.getId())) {
            throw new ResourceNotFoundException("Job with id: " + jobId + " does not belong to the user's profile");
        }

        // make sure it's analyzed for the profile
        job = jobService.fullAnalyze(jobId, userId, job.getProfile() == null, false, true);
        job = jobRepository.save(job);

        // Start building tailored CV
        TailoredCv tailoredCv = TailoredCv.builder()
                .profile(profile)
                .job(job)
                .personalDetails(profile.getPersonalDetails())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<MatchedSkill> matchedSkills = job.
                getSkillMatchingAnalyses().stream().filter(analysis -> analysis.getProfile() == profile)
                .findFirst().get().getMatchedSkills()
                .stream().sorted(Comparator.comparing(MatchedSkill::getSimilarity).reversed()).toList();

        List<UserSkill> tailoredSkills = matchedSkills.stream()
                .map(MatchedSkill::getUserSkill)
                .sorted(Comparator.comparing(UserSkill::getLevel, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        // convert to UserSkillCv
        tailoredCv.setSkillSection(
                SkillSection.builder().items(
                        tailoredSkills.stream().
                                map(userSkillCvMapper::mapFrom).
                                toList()
                ).sectionTitle("Skills").build());


        // Sort experiences by date
        List<Experience> tailoredExperience = profile.getExperienceList().stream()
                .sorted(Comparator.comparing(Experience::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        // Convert to ExperienceCv
        tailoredCv.setExperienceSection(
                ExperienceSection.builder().items(
                        tailoredExperience.stream()
                                .map(experienceCvMapper::mapFrom).
                                toList()
                ).sectionTitle("Experience").build());

        // Sort education by date
        List<Education> tailoredEducation = profile.getEducationList().stream()
                .sorted(Comparator.comparing(Education::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        // Convert to EducationCv
        tailoredCv.setEducationSection(
                EducationSection.builder().items(
                        tailoredEducation.stream()
                                .map(educationCvMapper::mapFrom)
                                .toList()
                ).sectionTitle("Education").build());

        // Include relevant projects
        List<Project> tailoredProjects = job.getProjectMatchingAnalyses().stream()
                .filter(analysis -> analysis.getProfile() == profile)
                .findFirst().get().getProjectsMatchedWithSkills()
                .stream().sorted(Comparator.comparing(MatchedProject::getMatchedSkillsCount).reversed())
                .map(MatchedProject::getProject).toList();

        // Convert to ProjectCv
        tailoredCv.setProjectSection(
                ProjectSection.builder().items(
                        tailoredProjects.stream()
                                .map(projectCvMapper::mapFrom).
                                toList()
                ).sectionTitle("Projects").build());

        // Generate summary
        String summaryText = generateProfileSummary(profile, job);
        tailoredCv.setSummarySection(
                SummarySection.builder()
                        .summary(summaryText)
                        .sectionTitle("Summary")
                        .build()
        );

        // Set order index
        setOrderIndex(
                tailoredCv.getEducationSection().getItems().stream()
                        .map(e -> (CvItem) e)
                        .toList()
        );

        setOrderIndex(
                tailoredCv.getExperienceSection().getItems().stream().
                        map(e -> (CvItem) e)
                        .toList()
        );

        setOrderIndex(
                tailoredCv.getProjectSection().getItems().stream()
                        .map(e -> (CvItem) e)
                        .toList()
        );

        setOrderIndex(
                tailoredCv.getSkillSection().getItems().stream()
                        .map(e -> (CvItem) e)
                        .toList()
        );

        // Set order index for sections
        setOrderIndexOfSections(tailoredCv);

        // Save and return
        tailoredCv = tailoredCvRepository.save(tailoredCv);
        tailoredCvRepository.updateCvTitle(tailoredCv.getId(), "Resume #" + tailoredCv.getId());
        tailoredCv.setCvTitle("Resume #" + tailoredCv.getId());
        return tailoredCv;
    }

    @Override
    @Transactional
    public TailoredCv generateCv(Long userId, boolean createEmptyCv) {
        // Check if CV already exists for this user and job
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Profile profile = user.getProfile();
        if (profile == null) throw new ResourceNotFoundException("User has no profile");

        // Start building tailored CV
        TailoredCv tailoredCv = TailoredCv.builder()
                .profile(profile)
                .job(null)
                .personalDetails(profile.getPersonalDetails())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (createEmptyCv) {
            tailoredCv.setSummarySection(
                    SummarySection.builder()
                            .summary("Fill in your summary here.")
                            .sectionTitle("Summary")
                            .build()
            );
            tailoredCv.setSkillSection(SkillSection.builder().sectionTitle("Skills").build());
            tailoredCv.setExperienceSection(ExperienceSection.builder().sectionTitle("Experience").build());
            tailoredCv.setEducationSection(EducationSection.builder().sectionTitle("Education").build());
            tailoredCv.setProjectSection(ProjectSection.builder().sectionTitle("Projects").build());
            setOrderIndexOfSections(tailoredCv);

            tailoredCv = tailoredCvRepository.save(tailoredCv);
            tailoredCvRepository.updateCvTitle(tailoredCv.getId(), "Resume #" + tailoredCv.getId());
            tailoredCv.setCvTitle("Resume #" + tailoredCv.getId());
            return tailoredCv;
        }

        List<UserSkill> tailoredSkills = profile.getUserSkills().stream()
                .sorted(Comparator.comparing(UserSkill::getLevel, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();


        // convert to UserSkillCv
        tailoredCv.setSkillSection(
                SkillSection.builder().items(
                        tailoredSkills.stream().
                                map(userSkillCvMapper::mapFrom).
                                toList()
                ).sectionTitle("Skills").build());


        // Sort experiences by date
        List<Experience> tailoredExperience = profile.getExperienceList().stream()
                .sorted(Comparator.comparing(Experience::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        // Convert to ExperienceCv
        tailoredCv.setExperienceSection(
                ExperienceSection.builder().items(
                        tailoredExperience.stream()
                                .map(experienceCvMapper::mapFrom).
                                toList()
                ).sectionTitle("Experience").build());

        // Sort education by date
        List<Education> tailoredEducation = profile.getEducationList().stream()
                .sorted(Comparator.comparing(Education::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        // Convert to EducationCv
        tailoredCv.setEducationSection(
                EducationSection.builder().items(
                        tailoredEducation.stream()
                                .map(educationCvMapper::mapFrom)
                                .toList()
                ).sectionTitle("Education").build());

        // Include relevant projects
        List<Project> tailoredProjects = profile.getProjects();

        // Convert to ProjectCv
        tailoredCv.setProjectSection(
                ProjectSection.builder().items(
                        tailoredProjects.stream()
                                .map(projectCvMapper::mapFrom).
                                toList()
                ).sectionTitle("Projects").build());

        // Generate summary
        String summaryText = generateProfileSummary(profile, null);
        tailoredCv.setSummarySection(
                SummarySection.builder()
                        .summary(summaryText)
                        .sectionTitle("Summary")
                        .build()
        );

        // Set order index
        setOrderIndex(
                tailoredCv.getEducationSection().getItems().stream()
                        .map(e -> (CvItem) e)
                        .toList()
        );

        setOrderIndex(
                tailoredCv.getExperienceSection().getItems().stream().
                        map(e -> (CvItem) e)
                        .toList()
        );

        setOrderIndex(
                tailoredCv.getProjectSection().getItems().stream()
                        .map(e -> (CvItem) e)
                        .toList()
        );

        setOrderIndex(
                tailoredCv.getSkillSection().getItems().stream()
                        .map(e -> (CvItem) e)
                        .toList()
        );

        // Set order index for sections
        setOrderIndexOfSections(tailoredCv);

        // Save and return
        tailoredCv = tailoredCvRepository.save(tailoredCv);
        tailoredCvRepository.updateCvTitle(tailoredCv.getId(), "Resume #" + tailoredCv.getId());
        tailoredCv.setCvTitle("Resume #" + tailoredCv.getId());
        return tailoredCv;
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
        return tailoredCvRepository.findByJobIdAndProfileId(jobId, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found for job: " + jobId + " and user: " + userId));
    }

    private String generateProfileSummary(Profile profile, Job job) {
        String summary = "Professional with experience in " +
                profile.getUserSkills().stream()
                        .limit(5)
                        .map(UserSkill::getSkill)
                        .collect(Collectors.joining(", "));
        if (job != null)
            summary += " seeking a position as " + job.getTitle() + " at " + job.getCompany() + ".";
        return summary;
    }

    @Override
    public TailoredCv updateCv(Long cvId, Long userId, TailoredCvDto tailoredCvDto) {
        // Get the CV and validate ownership
        TailoredCv tailoredCv = getCvByIdAndUserId(cvId, userId);
        if (tailoredCvDto.getCvTitle() != null) {
            tailoredCv.setCvTitle(tailoredCvDto.getCvTitle());
        }
        // update the CV with the provided data and save it
        if (tailoredCvDto.getPersonalDetails() != null) {
            tailoredCv.setPersonalDetails(tailoredCvDto.getPersonalDetails());
        }
        if (tailoredCvDto.getSummarySection() != null) {
            if (tailoredCv.getSummarySection() == null) tailoredCv.setSummarySection(new SummarySection());
            tailoredCv.getSummarySection().setHidden(tailoredCvDto.getSummarySection().isHidden());
            tailoredCv.getSummarySection().setSectionTitle(tailoredCvDto.getSummarySection().getSectionTitle());
            tailoredCv.getSummarySection().setOrderIndex(tailoredCvDto.getSummarySection().getOrderIndex());
            tailoredCv.getSummarySection().setSummary(tailoredCvDto.getSummarySection().getSummary());
        }

        if (tailoredCvDto.getEducationSection() != null) {
            if (tailoredCv.getEducationSection() == null) tailoredCv.setEducationSection(new EducationSection());
            tailoredCv.getEducationSection().setHidden(tailoredCvDto.getEducationSection().isHidden());
            tailoredCv.getEducationSection().setSectionTitle(tailoredCvDto.getEducationSection().getSectionTitle());
            tailoredCv.getEducationSection().setOrderIndex(tailoredCvDto.getEducationSection().getOrderIndex());

            // Create a new ArrayList instead of setting directly
            tailoredCv.getEducationSection().getItems().clear();
            tailoredCv.getEducationSection().getItems().addAll(tailoredCvDto.getEducationSection().getItems());
            tailoredCv.getEducationSection().getItems().forEach(e -> e.setId(null));
            validateOrSetOrderIndex(tailoredCv.getEducationSection().getItems().stream().map(e -> (CvItem) e).toList());
        }
        if (tailoredCvDto.getExperienceSection() != null) {
            if (tailoredCv.getExperienceSection() == null) tailoredCv.setExperienceSection(new ExperienceSection());
            tailoredCv.getExperienceSection().setHidden(tailoredCvDto.getExperienceSection().isHidden());
            tailoredCv.getExperienceSection().setSectionTitle(tailoredCvDto.getExperienceSection().getSectionTitle());
            tailoredCv.getExperienceSection().setOrderIndex(tailoredCvDto.getExperienceSection().getOrderIndex());

            // Create a new ArrayList instead of setting directly
            tailoredCv.getExperienceSection().getItems().clear();
            tailoredCv.getExperienceSection().getItems().addAll(tailoredCvDto.getExperienceSection().getItems());
            tailoredCv.getExperienceSection().getItems().forEach(e -> e.setId(null));
            validateOrSetOrderIndex(tailoredCv.getExperienceSection().getItems().stream().map(e -> (CvItem) e).toList());
        }
        if (tailoredCvDto.getSkillSection() != null) {
            if (tailoredCv.getSkillSection() == null) tailoredCv.setSkillSection(new SkillSection());
            tailoredCv.getSkillSection().setHidden(tailoredCvDto.getSkillSection().isHidden());
            tailoredCv.getSkillSection().setSectionTitle(tailoredCvDto.getSkillSection().getSectionTitle());
            tailoredCv.getSkillSection().setOrderIndex(tailoredCvDto.getSkillSection().getOrderIndex());

            // Create a new ArrayList instead of setting directly
            tailoredCv.getSkillSection().getItems().clear();
            tailoredCv.getSkillSection().getItems().addAll(tailoredCvDto.getSkillSection().getItems());
            tailoredCv.getSkillSection().getItems().forEach(e -> e.setId(null));
            validateOrSetOrderIndex(tailoredCv.getSkillSection().getItems().stream().map(e -> (CvItem) e).toList());
        }
        if (tailoredCvDto.getProjectSection() != null) {
            if (tailoredCv.getProjectSection() == null) tailoredCv.setProjectSection(new ProjectSection());
            tailoredCv.getProjectSection().setHidden(tailoredCvDto.getProjectSection().isHidden());
            tailoredCv.getProjectSection().setSectionTitle(tailoredCvDto.getProjectSection().getSectionTitle());
            tailoredCv.getProjectSection().setOrderIndex(tailoredCvDto.getProjectSection().getOrderIndex());

            // Create a new ArrayList instead of setting directly
            tailoredCv.getProjectSection().getItems().clear();
            tailoredCv.getProjectSection().getItems().addAll(tailoredCvDto.getProjectSection().getItems());
            tailoredCv.getProjectSection().getItems().forEach(item -> {
                item.setId(null);
                if (item.getSkills() != null) {
                    item.getSkills().forEach(skill -> skill.setId(null));
                    item.getSkills().forEach(skill -> skill.setProjectCv(item));
                }
            });
            validateOrSetOrderIndex(tailoredCv.getProjectSection().getItems().stream().map(e -> (CvItem) e).toList());
        }
        // Validate or set order index of sections
        validateOrSetOrderIndexOfSections(tailoredCv);
        // Set created and updated timestamps
        tailoredCv.setUpdatedAt(LocalDateTime.now());
        return tailoredCvRepository.save(tailoredCv);
    }

    @Override
    public void deleteCv(Long cvId, Long userId) {
        // Get the CV and validate ownership
        TailoredCv tailoredCv = getCvByIdAndUserId(cvId, userId);
        // Delete the CV
        tailoredCvRepository.delete(tailoredCv);
    }

    @Override
    public void updateCvTitle(Long cvId, Long userId, String title) {
        // Get the CV and validate ownership
        TailoredCv tailoredCv = getCvByIdAndUserId(cvId, userId);
        // Update the title
        tailoredCv.setCvTitle(title);
        // Save the updated CV
        tailoredCvRepository.save(tailoredCv);
    }

    private void setOrderIndex(List<CvItem> items) {
        for (int i = 0; i < items.size(); i++)
            items.get(i).setOrderIndex(i + 1);
    }

    private void setOrderIndexOfSections(TailoredCv cv) {
        cv.getSummarySection().setOrderIndex(1);
        cv.getEducationSection().setOrderIndex(2);
        cv.getExperienceSection().setOrderIndex(3);
        cv.getProjectSection().setOrderIndex(4);
        cv.getSkillSection().setOrderIndex(5);
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
        orderIndexes.add(cv.getSummarySection().getOrderIndex());
        orderIndexes.add(cv.getEducationSection().getOrderIndex());
        orderIndexes.add(cv.getExperienceSection().getOrderIndex());
        orderIndexes.add(cv.getProjectSection().getOrderIndex());
        orderIndexes.add(cv.getSkillSection().getOrderIndex());
        Integer minOrderIndex = Collections.min(orderIndexes);
        Integer maxOrderIndex = Collections.max(orderIndexes);
        if (orderIndexes.size() != 5 || orderIndexes.contains(null) || minOrderIndex <= 0 || maxOrderIndex > 5)
            setOrderIndexOfSections(cv);
    }
}