package com.Graduation.InstaCv.security;

import com.Graduation.InstaCv.service.Interfaces.IAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IAuthService authservice;

    // List of paths that should bypass JWT authentication
    private final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/v1/auth",       // Your existing auth endpoints
            "/api/github/authorize",
            "/api/github/callback",
            "/api/github/test",
            "/api/v1/jobs",
            "/api/v1/profiles",
            "/api/v1/cv"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Skip this filter for public paths
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);
            if (jwt != null) {
                UserDetails userDetails = authservice.validateToken(jwt);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                if (userDetails instanceof userDetails) {
                    request.setAttribute("userId", ((userDetails) userDetails).getId());
                }
            }
        }
        catch (Exception e) {
            log.warn("Received Invalid auth token", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}