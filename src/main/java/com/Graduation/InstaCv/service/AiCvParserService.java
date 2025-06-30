package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.ProfileDto;
import com.Graduation.InstaCv.utils.CvParsingPrompts;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AiCvParserService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String modelName;

    @Value("${cv.parser.max.text.length:4000}")
    private int maxTextLength;

    private final ObjectMapper objectMapper;
    private final CvParsingPrompts promptUtils;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private void validateApiKey() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-gemini-key-here")) {
            throw new IllegalStateException("Gemini API key not configured! Set GEMINI_API_KEY environment variable or update application.properties");
        }
    }

    public ProfileDto parseCV(MultipartFile pdfFile) throws IOException {
        validateApiKey();

        if (pdfFile.isEmpty()) {
            throw new IllegalArgumentException("PDF file is empty");
        }

        if (!pdfFile.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("File must be a PDF");
        }

        String cvText = extractTextFromPDF(pdfFile);

        if (cvText.trim().isEmpty()) {
            throw new IllegalArgumentException("Could not extract text from PDF");
        }

        return extractProfileWithAI(cvText);
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private ProfileDto extractProfileWithAI(String cvText) throws IOException {
        if (cvText.length() > maxTextLength) {
            cvText = cvText.substring(0, maxTextLength) + "...";
        }

        String prompt = promptUtils.getCvParsingPrompt(cvText);
        String jsonResponse = callGemini(prompt);
        jsonResponse = cleanJsonResponse(jsonResponse);

        try {
            ProfileDto profile = objectMapper.readValue(jsonResponse, ProfileDto.class);
            return postProcessProfile(profile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + jsonResponse, e);
        }
    }

    private String cleanJsonResponse(String response) {
        response = response.trim();
        if (response.startsWith("```json")) {
            response = response.substring(7);
        }
        if (response.startsWith("```")) {
            response = response.substring(3);
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.length() - 3);
        }
        int firstBrace = response.indexOf('{');
        int lastBrace = response.lastIndexOf('}');

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            response = response.substring(firstBrace, lastBrace + 1);
        }

        return response.trim();
    }

    private String callGemini(String prompt) throws IOException {
        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                modelName, apiKey
        );

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.1);
        generationConfig.put("topK", 1);
        generationConfig.put("topP", 1);
        generationConfig.put("maxOutputTokens", 10000);
        requestBody.put("generationConfig", generationConfig);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                throw new IOException("Gemini API error: " + response.code() + " - " + responseBody);
            }

            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                throw new IOException("No candidates in Gemini response");
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> candidateContent = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> candidateParts = (List<Map<String, Object>>) candidateContent.get("parts");

            if (candidateParts == null || candidateParts.isEmpty()) {
                throw new IOException("No parts in Gemini response");
            }

            return (String) candidateParts.get(0).get("text");
        }
    }

    private ProfileDto postProcessProfile(ProfileDto profile) {
        if (profile.getEducationList() != null) {
            profile.getEducationList().forEach(edu -> {
                if (edu.getEndDate() == null && !edu.isPresent()) {
                    edu.setPresent(true);
                }
            });
        }

        if (profile.getExperienceList() != null) {
            profile.getExperienceList().forEach(exp -> {
                if (exp.getEndDate() == null && !exp.isPresent()) {
                    exp.setPresent(true);
                }
            });
        }

        if (profile.getProjects() != null) {
            profile.getProjects().forEach(proj -> {
                if (proj.getEndDate() == null && !proj.isPresent()) {
                    proj.setPresent(true);
                }
            });
        }

        return profile;
    }
}