package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.UserDto;
import com.Graduation.InstaCv.data.dto.request.GithubAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.GithubAccessTokenResponse;
import com.Graduation.InstaCv.data.dto.response.GithubAuthLink;
import com.Graduation.InstaCv.data.dto.response.GithubUserResponse;
import com.Graduation.InstaCv.data.dto.response.LoginResponse;
import com.Graduation.InstaCv.data.enums.AuthProvider;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.github.GithubProfile;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.security.UserDetailsImpl;
import com.Graduation.InstaCv.security.UserDetailsInfo;
import com.Graduation.InstaCv.service.GithubService;
import com.Graduation.InstaCv.service.Interfaces.IAuthService;
import com.Graduation.InstaCv.service.Interfaces.IProfileService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github/test")
@RequiredArgsConstructor
public class GithubController {
    private final GithubService githubService;
    private final IProfileService profileService;
    private final IAuthService authService;
    @Value("${jwt.expiration.seconds}")
    private Long expiration;
    private final Mapper<User, UserDto> mapper;

    @GetMapping("/authorize")
    public ResponseEntity<GithubAuthLink> authorize(@RequestParam(defaultValue = "false") boolean isLogin) {
        return ResponseEntity.ok(githubService.getAuthorizationUrl(isLogin));
    }

    @GetMapping("/callback")
    public ResponseEntity<GithubAccessTokenResponse> callback(@RequestParam String code) {
        GithubAccessTokenResponse tokenResponse = githubService.getAccessToken(code);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/login-callback")
    public ResponseEntity<LoginResponse> loginCallback(@RequestParam String code) {
        GithubAccessTokenResponse tokenResponse = githubService.getAccessToken(code);
        GithubUserResponse profileInfo = githubService.getUserProfileInfo(tokenResponse);
        User user = authService.processOAuthPostLogin(
                profileInfo.getEmail(),
                profileInfo.getName(),
                AuthProvider.GITHUB
        );
        String token = authService.generateToken(new UserDetailsImpl(UserDetailsInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build()));
        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .expiresIn(expiration)
                .user(mapper.mapTo(user))
                .build());
    }


    @PostMapping("/profile")
    public ResponseEntity<GithubProfile> profile(@RequestBody GithubAccessTokenRequest request) {
        return ResponseEntity.ok(githubService.getUserProfile(request));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        profileService.deleteGithubProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
