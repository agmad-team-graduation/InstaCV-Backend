package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.request.RegistrationRequest;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.UserPhoto;
import com.Graduation.InstaCv.data.model.VerificationToken;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.exceptions.InvalidRegistrationDataException;
import com.Graduation.InstaCv.exceptions.InvalidTokenException;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.UserPhotoRepository;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.repository.VerificationTokenRepository;
import com.Graduation.InstaCv.service.Interfaces.IUserService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPhotoRepository userPhotoRepository;
    private final CloudinaryService cloudinaryService;

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
        user.getProfile().setUser(user);
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
} 