package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.request.LoginRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthService {
    UserDetails authenticate(LoginRequest loginRequest);
    String generateToken(UserDetails userDetails);
    UserDetails validateToken(String token);
}
