package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.request.LoginRequest;
import com.Graduation.InstaCv.data.dto.request.RegistrationRequest;
import com.Graduation.InstaCv.data.dto.response.LoginResponse;
import com.Graduation.InstaCv.data.dto.response.RegisterResponse;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.service.Interfaces.IAuthService;
import com.Graduation.InstaCv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final IAuthService authService;

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authService.authenticate(loginRequest);
        String token = authService.generateToken(userDetails);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .expiresIn(86400L)
                .build();

        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegistrationRequest request) {
        User registeredUser = userService.registerUser(request);

        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(registeredUser.getId())
                .email(registeredUser.getEmail())
                .name(registeredUser.getName())
                .build();

        return ResponseEntity.ok(registerResponse);
    }

}
