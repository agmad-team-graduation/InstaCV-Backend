package com.Graduation.InstaCv.data.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkedinAccessTokenRequest {
    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;
    private String clientSecret;
}
