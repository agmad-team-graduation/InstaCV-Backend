package com.Graduation.InstaCv.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequest {


    @NotBlank(message = "name is required")
    private String name ;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

}