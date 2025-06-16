package com.Graduation.InstaCv.gateways;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign configuration for Groq API client.
 * Automatically adds Authorization header from environment variable.
 */
@Configuration
public class GroqFeignConfig {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Bean
    public RequestInterceptor groqRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("Authorization", "Bearer " + groqApiKey);
            }
        };
    }
} 