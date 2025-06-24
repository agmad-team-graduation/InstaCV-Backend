package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.request.LoginRequest;
import com.Graduation.InstaCv.data.enums.AuthProvider;
import com.Graduation.InstaCv.data.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthService {
    UserDetails authenticate(LoginRequest loginRequest);
    String generateToken(UserDetails userDetails);
    Claims extractClaims(String token);
    User processOAuthPostLogin(String email, String name, AuthProvider provider);
}
