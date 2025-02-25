package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.dto.request.RegistrationRequest;
import com.Graduation.InstaCv.model.User;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.service.Interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegistrationRequest registrationDto) {
        // Check if user already exists
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + registrationDto.getEmail());
        }

        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        return userRepository.save(user);
    }
} 