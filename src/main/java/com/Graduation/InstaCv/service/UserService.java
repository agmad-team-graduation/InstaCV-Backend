package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.request.RegistrationRequest;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.UserPhoto;
import com.Graduation.InstaCv.data.model.VerificationToken;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.exceptions.InvalidRegistrationDataException;
import com.Graduation.InstaCv.exceptions.InvalidTokenException;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.*;
import com.Graduation.InstaCv.service.Interfaces.IUserService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPhotoRepository userPhotoRepository;
    private final CloudinaryService cloudinaryService;
    private final ProfileRepository profileRepository;
    private final TailoredCvRepository tailoredCvRepository;
    private final JobRepository jobRepository;
    private final GithubProfileRepository githubProfileRepository;

    @Override
    public User registerUser(RegistrationRequest request) {

        String token = request.getVerificationToken();

        // 1. Check if token exists, is valid and not used

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification token"));

        String email = verificationToken.getEmail();
        String name = verificationToken.getName();

        // 1. Validate registration data
        if (email == null || email.isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                name == null || name.isBlank()) {
            throw new InvalidRegistrationDataException("All fields are required: email, password, and name");
        }

        // check the verification token
        emailVerificationService.verifyEmail(token);
        // 5. Create and save the user
        return createNewUser(email, name, request.getPassword());
    }

    public User createNewUser(String email, String name, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .isProfileCreated(false)
//                .profile(Profile.builder()
//                        .personalDetails(PersonalDetails.builder().fullName(name).email(email).build())
//                        .build())
                .password(passwordEncoder.encode(password))
                .build();
//        user.getProfile().setUser(user);
        return userRepository.save(user);
    }

    public UserPhoto uploadUserPhoto(MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Optional<User> user = userRepository.findById(userId);

        // Check if user already has a photo
        Optional<UserPhoto> existingPhoto = userPhotoRepository.findByUserId(userId);

        // Delete old photo if exists
        if (existingPhoto.isPresent()) {
            deleteUserPhoto();
        }

        // Upload new photo to Cloudinary
        Map<String, Object> uploadResult = cloudinaryService.uploadPhoto(file);

        // Create new UserPhoto entity
        UserPhoto userPhoto = UserPhoto.builder()
                .user(user.get())
                .photoUrl((String) uploadResult.get("secure_url"))
                .photoPublicId((String) uploadResult.get("public_id"))
                .photoFormat((String) uploadResult.get("format"))
                .photoSize(((Number) uploadResult.get("bytes")).longValue())
                .width((Integer) uploadResult.get("width"))
                .height((Integer) uploadResult.get("height"))
                .resourceType((String) uploadResult.get("resource_type"))
                .cloudinaryVersion(uploadResult.get("version").toString())
                .uploadedAt(new Date())
                .build();
        return userPhotoRepository.save(userPhoto);
    }

    public void deleteUserPhoto() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        Optional<User> user = userRepository.findById(userId);

        UserPhoto userPhoto = userPhotoRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found for user: " + userId));

        // Delete from Cloudinary
        cloudinaryService.deletePhoto(userPhoto.getPhotoPublicId());

        user.get().setPhoto(null);

        // Delete from database
        userPhotoRepository.delete(userPhoto);
    }

    public UserPhoto getUserPhoto() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        return userPhotoRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found for user: " + userId));
    }

    public boolean hasPhoto() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        return userPhotoRepository.existsByUserId(userId);
    }

    public User getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    public void saveUserPhoto(UserPhoto photo) {
        User user = photo.getUser();
        // Delete old photo if exists
        userPhotoRepository.findByUserId(user.getId()).ifPresent(oldPhoto -> {
            if (oldPhoto.getPhotoPublicId() != null) {
                cloudinaryService.deletePhoto(oldPhoto.getPhotoPublicId());
            }
            userPhotoRepository.delete(oldPhoto);
        });

        // Save new photo
        user.setPhoto(photo);
        userPhotoRepository.save(photo);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    @Transactional
    public void deleteUser(Long userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        // Delete user photo from Cloudinary and database
//        userPhotoRepository.findByUserId(userId).ifPresent(userPhoto -> {
//            log.info("Deleting user photo from Cloudinary and database");
//            if (userPhoto.getPhotoPublicId() != null) {
//                cloudinaryService.deletePhoto(userPhoto.getPhotoPublicId());
//            }
//            userPhotoRepository.delete(userPhoto);
//        });
//

        // Delete profile and all related data (cascade will handle most of it)
        profileRepository.findByUserId(userId).ifPresent(profile -> {
            // Delete skill and project matching analyses for external jobs (scraped jobs)
            List<Job> analyzedScrapedJobs = jobRepository.findAnalyzedScrapedJobsByProfileId(profile.getId());
            analyzedScrapedJobs.forEach(job -> {
                // Remove skill matching analyses for this profile
                job.getSkillMatchingAnalyses().removeIf(analysis -> analysis.getProfile().getId().equals(profile.getId()));
                // Remove project matching analyses for this profile
                job.getProjectMatchingAnalyses().removeIf(analysis -> analysis.getProfile().getId().equals(profile.getId()));
                jobRepository.save(job);
            });

            // Delete all CVs associated with this profile
            List<TailoredCv> cvs = tailoredCvRepository.findByProfileId(profile.getId());
            log.info("Deleting {} CVs associated with profile", cvs.size());
            tailoredCvRepository.deleteAll(cvs);

            List<Job> scrapedJobs = jobRepository.findJobsByProfileId(profile.getId());
            log.info("Deleting {} scraped jobs associated with profile", scrapedJobs.size());
            jobRepository.deleteAll(scrapedJobs);

            // Delete all jobs added by this user (jobs with profile_id = profile.getId())
            List<Job> userJobs = jobRepository.findJobsByProfileId(profile.getId());
            log.info("Deleting {} jobs added by user", userJobs.size());
            jobRepository.deleteAll(userJobs);

            // Delete GitHub profile if exists
            if (profile.getGithubProfile() != null) {
                log.info("Deleting GitHub profile: {}", profile.getGithubProfile().getUsername());
                githubProfileRepository.delete(profile.getGithubProfile());
            }

            // Delete the profile (this will cascade delete education, experience, skills, projects)
            profileRepository.delete(profile);
        });

        userRepository.delete(user);
    }
} 