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
                "  \"match_score\": 63,\n" +
                "  \"ats_eval\": \"Partial\",\n" +
                "  \"recruiter_eval\": \"6/10\",\n" +
                "  \"shortlist_eval\": \"Moderate\",\n" +
                "  \"verdict\": \"Apply with tweaks\",\n" +
                "  \"general_feedback\": \"This is a business + tech hybrid role...\",\n" +
                "  \"exact_fixes\": [\n" +
                "    {\n" +
                "      \"title\": \"Summary bullet 3 — replace entirely.\",\n" +
                "      \"location\": \"Summary, line 3\",\n" +
                "      \"type\": \"replace\",\n" +
                "      \"originalText\": \"Seeking a software engineering internship...\",\n" +
                "      \"newText\": \"Experienced in translating business requirements...\",\n" +
                "      \"keywords\": [\"business requirements\", \"stakeholder collaboration\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"title\": \"Internship bullet 3 — add stakeholder language.\",\n" +
                "      \"location\": \"Internship, bullet 3\",\n" +
                "      \"type\": \"add\",\n" +
                "      \"originalText\": \"\",\n" +
                "      \"newText\": \"communicated project progress to stakeholders...\",\n" +
                "      \"keywords\": [\"stakeholder communication\", \"proactive\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return callGeminiAPI(prompt);
    }

    public Map<String, Object> analyzeResume(String resumeText) {
        String prompt = "You are an expert ATS (Applicant Tracking System) and technical recruiter. " +
                "Analyze the following resume.\n\n" +
                "Resume:\n" + resumeText + "\n\n" +
                "Provide a JSON response strictly with the following format:\n" +
                "{\n" +
                "  \"match_score\": 80,\n" +
                "  \"ats_eval\": \"Pass\",\n" +
                "  \"recruiter_eval\": \"8/10\",\n" +
                "  \"shortlist_eval\": \"High\",\n" +
                "  \"verdict\": \"Strong candidate\",\n" +
                "  \"general_feedback\": \"Your resume is well structured...\",\n" +
                "  \"exact_fixes\": [\n" +
                "    {\n" +
                "      \"title\": \"General - Add more metrics\",\n" +
                "      \"location\": \"Experience section\",\n" +
                "      \"type\": \"add\",\n" +
                "      \"originalText\": \"\",\n" +
                "      \"newText\": \"Increased performance by 20%\",\n" +
                "      \"keywords\": [\"metrics\", \"quantifiable\"]\n" +
                "    }\n" +
                "  ]\n" +
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
            } else if (textResponse.startsWith("```")) {
                textResponse = textResponse.substring(3, textResponse.length() - 3).trim();
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
