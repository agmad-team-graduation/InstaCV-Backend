package com.Graduation.InstaCv.data.dto.response.linkedin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedinProfileResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String headline;
    private String profilePictureUrl;
}
