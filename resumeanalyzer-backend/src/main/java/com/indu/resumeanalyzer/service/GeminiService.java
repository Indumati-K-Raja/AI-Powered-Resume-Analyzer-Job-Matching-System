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

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyzeResumeWithJD(String resumeText, String jdText) {
        log.info("Analyzing resume with Live Gemini API...");
        
        String prompt = "You are an expert ATS (Applicant Tracking System) and technical recruiter. " +
                "Analyze the following resume against the given job description.\n\n" +
                "Resume:\n" + resumeText + "\n\n" +
                "Job Description:\n" + jdText + "\n\n" +
                "Provide a JSON response strictly with the following format (No extra text, just JSON):\n" +
                "{\n" +
                "  \"resumeScore\": 85,\n" +
                "  \"atsEval\": \"Excellent\",\n" +
                "  \"recruiterEval\": \"9/10\",\n" +
                "  \"shortlistEval\": \"High\",\n" +
                "  \"verdict\": \"Strong Candidate\",\n" +
                "  \"generalFeedback\": \"Your profile is a strong match...\",\n" +
                "  \"exactFixes\": [\n" +
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
            log.error("Gemini API Key is missing!");
            return Map.of("error", "AI Error: API Key missing.");
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
            
            // Removed response_mime_type for universal compatibility
            requestMap.put("generationConfig", new HashMap<>());

            String requestBody = objectMapper.writeValueAsString(requestMap);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String textResponse = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            // Handle potential markdown formatting or raw JSON
            String jsonContent = textResponse;
            if (jsonContent.contains("```json")) {
                jsonContent = jsonContent.split("```json")[1].split("```")[0].trim();
            } else if (jsonContent.contains("```")) {
                jsonContent = jsonContent.split("```")[1].split("```")[0].trim();
            }

            return objectMapper.readValue(jsonContent, Map.class);
        } catch (HttpClientErrorException e) {
            log.error("Gemini API Error: {}", e.getResponseBodyAsString());
            return Map.of("error", "AI Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Critical Gemini Error: ", e);
            return Map.of("error", "System Error: " + e.getMessage());
        }
    }
}
