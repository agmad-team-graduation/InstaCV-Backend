package com.Graduation.InstaCv.data.dto.request;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String password;
    private String verificationToken;
} 