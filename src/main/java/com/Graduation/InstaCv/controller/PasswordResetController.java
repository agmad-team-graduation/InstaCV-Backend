package com.Graduation.InstaCv.controller;


import com.Graduation.InstaCv.data.dto.request.ForgotPasswordRequest;
import com.Graduation.InstaCv.data.dto.request.ResetPasswordRequest;
import com.Graduation.InstaCv.data.dto.response.MessageResponse;
import com.Graduation.InstaCv.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        boolean result = passwordResetService.processForgotPassword(request.getEmail());

        if (result) {
            return ResponseEntity.ok(new MessageResponse("Password reset email sent successfully"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Email not found"));
        }
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateResetToken(token);

        if (isValid) {
            return ResponseEntity.ok(new MessageResponse("Valid reset token"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired reset token"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean result = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

        if (result) {
            return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired token"));
        }
    }
}
