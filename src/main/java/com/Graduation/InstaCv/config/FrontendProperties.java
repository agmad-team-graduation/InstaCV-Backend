package com.Graduation.InstaCv.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "frontend")
@Data
public class FrontendProperties {
    private String resetUrl;
    private String oauth2SuccessUrl;
    private String verificationUrl;
}
