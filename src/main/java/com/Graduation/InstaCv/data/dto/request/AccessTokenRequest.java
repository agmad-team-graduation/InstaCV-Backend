package com.Graduation.InstaCv.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenRequest {
    private String client_id;
    private String client_secret;
    private String code;
}
