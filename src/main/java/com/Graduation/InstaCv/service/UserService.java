package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.request.RegistrationRequest;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.exceptions.EmailAlreadyExistsException;
import com.Graduation.InstaCv.exceptions.InvalidRegistrationDataException;
import com.Graduation.InstaCv.exceptions.ResourceNotFoundException;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegistrationRequest request) {

        String email = request.getEmail();
        // 1. Validate registration data
        if (email == null || email.isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRegistrationDataException("All fields are required: email, password, and name");
        }

        // check the verification token
        emailVerificationService.verifyEmail(request.getVerificationToken());
        // 5. Create and save the user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        return userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

} 