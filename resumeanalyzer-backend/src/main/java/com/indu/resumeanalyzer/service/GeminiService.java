package com.indu.resumeanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    @Value("${google.gemini.api-key}")
    private String apiKey;

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyzeResumeWithJD(String resumeText, String jdText) {
        log.info("Analyzing resume with Live Gemini API...");
        
        String prompt = "You are an expert ATS (Applicant Tracking System) and technical recruiter. " +
                "Analyze the following resume against the given job description.\n\n" +
                "Resume:\n" + resumeText + "\n\n" +
                "Job Description:\n" + jdText + "\n\n" +
                "Provide a JSON response strictly with the following format:\n" +
                "{\n" +
                "  \"match_score\": 85,\n" +
                "  \"ats_eval\": \"High\",\n" +
                "  \"recruiter_eval\": \"9/10\",\n" +
                "  \"shortlist_eval\": \"Likely\",\n" +
                "  \"verdict\": \"Strong Candidate\",\n" +
                "  \"general_feedback\": \"Your profile is a strong match...\",\n" +
                "  \"exact_fixes\": [\n" +
                "    {\n" +
                "      \"title\": \"Header Fix\",\n" +
                "      \"location\": \"Top section\",\n" +
                "      \"type\": \"replace\",\n" +
                "      \"originalText\": \"Software dev\",\n" +
                "      \"newText\": \"Senior Full Stack Engineer\",\n" +
                "      \"keywords\": [\"fullstack\", \"react\", \"java\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return callGeminiAPI(prompt);
    }

    public Map<String, Object> analyzeResume(String resumeText) {
        return analyzeResumeWithJD(resumeText, "General technical roles");
    }

    private Map<String, Object> callGeminiAPI(String prompt) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("${")) {
            log.error("Gemini API Key is missing or improperly configured! Please set the GEMINI_API_KEY environment variable.");
            return Map.of("error", "AI Configuration Error: API Key is missing. Please set the GEMINI_API_KEY environment variable.");
        }

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

            if (textResponse.contains("```json")) {
                textResponse = textResponse.split("```json")[1].split("```")[0].trim();
            } else if (textResponse.contains("```")) {
                textResponse = textResponse.split("```")[1].split("```")[0].trim();
            }

            return objectMapper.readValue(textResponse, Map.class);
        } catch (HttpClientErrorException e) {
            log.error("Gemini API Error ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            return Map.of("error", "AI Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Critical Gemini Error: ", e);
            return Map.of("error", "System Error: " + e.getMessage());
        }
    }
}
