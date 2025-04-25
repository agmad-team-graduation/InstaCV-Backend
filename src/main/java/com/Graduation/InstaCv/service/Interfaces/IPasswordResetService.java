package com.Graduation.InstaCv.service.Interfaces;

public interface IPasswordResetService {
    void processForgotPassword(String email);

    void resetPassword(String token, String newPassword);
}
