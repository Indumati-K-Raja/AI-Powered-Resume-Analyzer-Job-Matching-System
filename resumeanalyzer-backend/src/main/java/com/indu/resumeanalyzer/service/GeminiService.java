package com.indu.resumeanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${google.gemini.api-key}")
    private String apiKey;

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyzeResumeWithJD(String resumeText, String jdText) {
        String prompt = "You are an expert ATS (Applicant Tracking System) and technical recruiter. " +
                "Analyze the following resume against the given job description.\n\n" +
                "Resume:\n" + resumeText + "\n\n" +
                "Job Description:\n" + jdText + "\n\n" +
                "Provide a JSON response strictly with the following format:\n" +
                "{\n" +
                "  \"resume_skills\": [\"skill1\", \"skill2\"],\n" +
                "  \"missing_skills\": [\"skill3\", \"skill4\"],\n" +
                "  \"resume_score\": 85,\n" +
                "  \"job_fit_score\": 75,\n" +
                "  \"suggestions\": {\"skill3\": \"suggestion to learn skill3\", \"skill4\": \"suggestion to learn skill4\"}\n" +
                "}";

        return callGeminiAPI(prompt);
    }

    public Map<String, Object> analyzeResume(String resumeText) {
        String prompt = "You are an expert ATS (Applicant Tracking System) and technical recruiter. " +
                "Analyze the following resume.\n\n" +
                "Resume:\n" + resumeText + "\n\n" +
                "Provide a JSON response strictly with the following format:\n" +
                "{\n" +
                "  \"skills_found\": [\"skill1\", \"skill2\"],\n" +
                "  \"score\": 80,\n" +
                "  \"suggestions\": {\"skill1\": \"suggestion to improve skill1 usage\", \"general\": \"general resume tip\"}\n" +
                "}";

        return callGeminiAPI(prompt);
    }

    private Map<String, Object> callGeminiAPI(String prompt) {
        try {
            String url = GEMINI_API_URL + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestMap = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> contentMap = new HashMap<>();
            List<Map<String, Object>> parts = new ArrayList<>();
            Map<String, Object> partMap = new HashMap<>();
            partMap.put("text", prompt);
            parts.add(partMap);
            contentMap.put("parts", parts);
            contents.add(contentMap);
            requestMap.put("contents", contents);

            String requestBody = objectMapper.writeValueAsString(requestMap);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String textResponse = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            // Extract JSON from the markdown block if present
            if (textResponse.startsWith("```json")) {
                textResponse = textResponse.substring(7, textResponse.length() - 3).trim();
            }

            return objectMapper.readValue(textResponse, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "Failed to analyze with Gemini API: " + e.getMessage());
            return errorMap;
        }
    }
}
