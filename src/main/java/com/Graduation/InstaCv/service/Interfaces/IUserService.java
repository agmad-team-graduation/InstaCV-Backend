package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.dto.request.RegistrationRequest;
import com.Graduation.InstaCv.model.User;

public interface IUserService {
    User registerUser(RegistrationRequest registrationDto);
} 