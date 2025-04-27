package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.request.ForgotPasswordRequest;
import com.Graduation.InstaCv.data.dto.request.ResetPasswordRequest;
import com.Graduation.InstaCv.data.dto.response.MessageResponse;
import com.Graduation.InstaCv.service.Interfaces.IPasswordResetService;
import com.Graduation.InstaCv.service.PasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class PasswordResetController {
    private IPasswordResetService passwordResetService;

    @PostMapping("/forget-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.processForgotPassword(request.getEmail());

        return ResponseEntity.ok(
                new MessageResponse("If that email is registered, you will receive a reset link shortly.")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
    }
}
