package com.Graduation.InstaCv.config;

import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.security.JwtAuthenticationFilter;
import com.Graduation.InstaCv.security.UserDetailsServiceImpl;
import com.Graduation.InstaCv.service.Interfaces.IAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(IAuthService authService) {
        return new JwtAuthenticationFilter(authService);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

    private static final String[] WHITELIST_URLS = {
            // Existing auth endpoints
            "/api/v1/auth/**",
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/reset-password/validate",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/forget-password",

            // GitHub OAuth endpoints
            "/api/github/authorize",
            "/api/github/callback",
            "/api/github/test/**",

            // TODO: Remove unnecessary endpoints from the whitelist
            "/api/v1/jobs/**",
//            "/api/v1/profiles/**",
            "/api/v1/cv/**"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oauth2SuccessHandler(JwtTokenProvider tokenProvider, IAuthService authService) {
        return new OAuth2AuthenticationSuccessHandler(tokenProvider, authService);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   OAuth2AuthenticationSuccessHandler successHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Enable CORS with default settings
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(end -> end.baseUri("/api/auth/oauth2/authorize"))
                        .redirectionEndpoint(redir -> redir.baseUri("/api/auth/oauth2/code/*"))
                        .userInfoEndpoint(user -> user
                                .userService(new CustomOAuth2UserService())    // maps Google user to your User entity
                        )
                        .successHandler(successHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
