package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.request.RegistrationRequest;
import com.Graduation.InstaCv.data.model.User;

public interface IUserService {
    User registerUser(RegistrationRequest registrationDto);
} 