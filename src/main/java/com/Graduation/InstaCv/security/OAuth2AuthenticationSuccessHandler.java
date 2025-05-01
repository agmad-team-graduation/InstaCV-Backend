package com.Graduation.InstaCv.security;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.service.Interfaces.IAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class OAuth2AuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final IAuthService authService;

    public OAuth2AuthenticationSuccessHandler(IAuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        // 1. register or fetch local user
        User user = authService.processOAuthPostLogin(oauthUser.getAttribute("email"));
        // 2. generate JWT
        String token = authService.generateToken(new UserDetailsImpl(UserDetailsInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .build()));
        // 3. send token in response body or cookie
        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + token + "\"}");
    }
}
