package com.Graduation.InstaCv.service;


import com.Graduation.InstaCv.data.dto.request.LoginRequest;
import com.Graduation.InstaCv.data.enums.AuthProvider;
import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.exceptions.InvalidCredentialsException;
import com.Graduation.InstaCv.repository.UserRepository;
import com.Graduation.InstaCv.security.UserDetailsImpl;
import com.Graduation.InstaCv.service.Interfaces.IAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.milliseconds}")
    private Long expiration;

    @Override
    public UserDetails authenticate(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            return (UserDetails) authentication.getPrincipal();
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setId(((UserDetailsImpl) userDetails).getId().toString())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public User processOAuthPostLogin(String email, String name) { // add to database if not exists
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = User.builder()
                    .email(email)
                    .name(name)
                    .authProvider(AuthProvider.GOOGLE)
                    .build();
            return userRepository.save(user);
        });
    }
}
