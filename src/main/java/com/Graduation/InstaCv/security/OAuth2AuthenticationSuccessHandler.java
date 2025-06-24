package com.Graduation.InstaCv.security;

import com.Graduation.InstaCv.config.FrontendProperties;
import com.Graduation.InstaCv.data.enums.AuthProvider;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.service.Interfaces.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final IAuthService authService;
    private final FrontendProperties frontendProps;
    @Value("${jwt.expiration.seconds}")
    private Long expiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        // 1. register or fetch local user
        User user = authService.processOAuthPostLogin(oauthUser.getAttribute("email"), oauthUser.getAttribute("name"), AuthProvider.GOOGLE); // only Google for now
        // 2. generate JWT
        String token = authService.generateToken(new UserDetailsImpl(UserDetailsInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build()));

        // 3. Redirect with token and expiry in query string
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendProps.getOauth2SuccessUrl())
                .queryParam("token", token)
                .queryParam("expiresIn", expiration)
                .queryParam("provider", "google") // only Google for now
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
