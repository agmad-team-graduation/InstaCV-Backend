package com.Graduation.InstaCv.gateways;

import com.Graduation.InstaCv.data.dto.request.GroqChatCompletionRequest;
import com.Graduation.InstaCv.data.dto.response.JobllmResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Feign client to communicate with Groq LLM chat completions endpoint.
 */
@FeignClient(name = "GroqChatCompletionClient", url = "https://api.groq.com/openai/v1", configuration = GroqFeignConfig.class)
public interface GroqChatCompletionClient {

    /**
     * Calls /chat/completions endpoint and returns the raw JSON response as String.
     * Authorization header will be automatically added by GroqFeignConfig.
     *
     * @param request body of the request
     * @return raw JSON response from Groq as String
     */
    @PostMapping(value = "/chat/completions", consumes = "application/json")
    String chatCompletion(@RequestBody GroqChatCompletionRequest request);

    /**
     * Convenience wrapper that builds the {@link GroqChatCompletionRequest} from the provided message contents.
     */
    default String chatCompletion(String systemContent, String userContent) {
        GroqChatCompletionRequest request = GroqChatCompletionRequest.builder()
                .messages(List.of(
                        new GroqChatCompletionRequest.Message("system", systemContent),
                        new GroqChatCompletionRequest.Message("user", userContent)
                ))
                .model("qwen/qwen3-32b")
                .temperature(0.6)
                .maxCompletionTokens(4096)
                .topP(0.95)
                .stream(true)
                .reasoningEffort("default")
                .stop(null)
                .build();
        return chatCompletion(request);
    }

    /**
     * Parses the raw JSON response from Groq into a JobllmResponseDTO.
     * This method should be implemented to handle the specific structure of the Groq response.
     *
     * @param llmResponse the raw JSON response from Groq
     * @return a JobllmResponseDTO containing the parsed data
     */
    default JobllmResponseDTO parseJobLlmResponse(String llmResponse) {
        ObjectMapper mapper = new ObjectMapper()
                // if your JSON uses snake_case field names
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        try {
            // deserialize directly into your DTO
            return mapper.readValue(llmResponse, JobllmResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse LLM JSON response", e);
        }
    }

}