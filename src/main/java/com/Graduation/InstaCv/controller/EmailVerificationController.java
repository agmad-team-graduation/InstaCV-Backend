package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.request.EmailVerificationRequest;
import com.Graduation.InstaCv.data.dto.response.MessageResponse;
import com.Graduation.InstaCv.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-verification")
    public ResponseEntity<MessageResponse> sendVerificationEmail(@RequestBody EmailVerificationRequest request) {
        emailVerificationService.sendVerificationEmail(request.getName(), request.getEmail());
        return ResponseEntity.ok(
                new MessageResponse("Verification email has been sent to " + request.getEmail())
        );
    }

}