package com.Graduation.InstaCv.gateways;

import com.Graduation.InstaCv.data.dto.request.GroqChatCompletionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                .model("llama-3.3-70b-versatile")
                .temperature(0.6)
                .maxCompletionTokens(4096)
                .topP(0.95)
                .stream(false)
                .stop(null)
                .build();
        return chatCompletion(request);
    }

    /**
     * Extracts the plain text content from the Groq JSON response.
     *
     * @param llmResponse the raw JSON response from Groq
     * @return the plain text content from the assistant's response
     */
    default String extractPlainTextContent(String llmResponse) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Parse the JSON response
            var responseNode = mapper.readTree(llmResponse);
            
            // Extract content from choices[0].message.content
            var choicesArray = responseNode.get("choices");
            if (choicesArray != null && choicesArray.isArray() && choicesArray.size() > 0) {
                var firstChoice = choicesArray.get(0);
                var message = firstChoice.get("message");
                if (message != null) {
                    var content = message.get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
            
            throw new IllegalStateException("No content found in LLM response");
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse LLM JSON response", e);
        }
    }

    /**
     * Extracts only JSON content from the LLM response, filtering out any reasoning or thinking content.
     * This method looks for JSON objects in the response and returns the first valid JSON found.
     *
     * @param llmResponse the raw JSON response from Groq
     * @return the JSON content as a string, without any reasoning or thinking text
     */
    default String extractJsonContent(String llmResponse) {
        String plainText = extractPlainTextContent(llmResponse);
        return extractJsonFromText(plainText);
    }

    /**
     * Extracts and parses JSON content from the LLM response directly into a DTO.
     *
     * @param llmResponse the raw JSON response from Groq
     * @param responseType the class type to parse the JSON into
     * @return the parsed DTO object
     */
    default <T> T extractAndParseJson(String llmResponse, Class<T> responseType) {
        String jsonContent = extractJsonContent(llmResponse);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonContent, responseType);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse JSON content into " + responseType.getSimpleName() + ": " + jsonContent, e);
        }
    }

    /**
     * Extracts JSON content from a text that may contain reasoning, thinking, or other non-JSON content.
     * 
     * @param text the text that may contain JSON
     * @return the extracted JSON string
     */
    default String extractJsonFromText(String text) {
        // Remove any thinking tags and their content
        text = text.replaceAll("(?s)<think>.*?</think>", "").trim();
        
        // Try to find JSON object bounds
        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');
        
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            String possibleJson = text.substring(firstBrace, lastBrace + 1);
            
            // Validate that it's proper JSON by trying to parse it
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.readTree(possibleJson);
                return possibleJson;
            } catch (JsonProcessingException e) {
                // If the simple extraction doesn't work, try a more sophisticated approach
                return extractJsonWithRegex(text);
            }
        }
        
        // Fallback to regex-based extraction
        return extractJsonWithRegex(text);
    }
    
    /**
     * Uses regex to find the first valid JSON object in the text.
     * 
     * @param text the text containing JSON
     * @return the extracted JSON string
     */
    default String extractJsonWithRegex(String text) {
        // Pattern to match JSON objects (handles nested braces)
        Pattern jsonPattern = Pattern.compile("\\{(?:[^{}]|\\{[^{}]*\\})*\\}");
        Matcher matcher = jsonPattern.matcher(text);
        
        ObjectMapper mapper = new ObjectMapper();
        
        while (matcher.find()) {
            String candidateJson = matcher.group();
            try {
                // Validate by parsing
                mapper.readTree(candidateJson);
                return candidateJson;
            } catch (JsonProcessingException e) {
                // Continue to next match
            }
        }
        
        throw new IllegalStateException("No valid JSON found in LLM response: " + text);
    }

}