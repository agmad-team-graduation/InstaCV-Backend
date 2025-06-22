package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.UserDto;
import com.Graduation.InstaCv.data.dto.request.LoginRequest;
import com.Graduation.InstaCv.data.dto.request.RegistrationRequest;
import com.Graduation.InstaCv.data.dto.response.LoginResponse;
import com.Graduation.InstaCv.data.dto.response.RegisterResponse;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.security.UserDetailsImpl;
import com.Graduation.InstaCv.service.Interfaces.IAuthService;
import com.Graduation.InstaCv.service.Interfaces.IUserService;
import com.Graduation.InstaCv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final IAuthService authService;
    private final UserService userService;
    @Value("${jwt.expiration.seconds}")
    private Long expiration;
    private final Mapper<User, UserDto> mapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authService.authenticate(loginRequest);
        String token = authService.generateToken(userDetails);
        User user = userService.getUserById(((UserDetailsImpl) userDetails).getId());

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .expiresIn(expiration)
                .user(mapper.mapTo(user))
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

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(mapper.mapTo(userService.getCurrentUser()));
    }
}
