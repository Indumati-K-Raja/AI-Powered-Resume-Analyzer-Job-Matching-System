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

    // We will try these models in order of preference
    private final String[] MODELS = {
        "gemini-1.5-flash-latest",
        "gemini-1.5-flash",
        "gemini-pro"
    };

    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyzeResumeWithJD(String resumeText, String jdText) {
        log.info("Starting Resilient AI Analysis...");
        
        String prompt = "You are an expert ATS (Applicant Tracking System) and recruiter.\n" +
                "Analyze this Resume against the Job Description.\n\n" +
                "RESUME:\n" + resumeText + "\n\n" +
                "JOB DESCRIPTION:\n" + jdText + "\n\n" +
                "Return ONLY a JSON object with these keys: resumeScore (0-100), atsEval, recruiterEval, shortlistEval, verdict, generalFeedback, exactFixes (array of {title, location, type, originalText, newText, keywords}).";

        // Try models one by one until one works
        for (String model : MODELS) {
            try {
                log.info("Attempting analysis with model: {}", model);
                return callGeminiAPI(model, prompt);
            } catch (HttpClientErrorException.NotFound e) {
                log.warn("Model {} not found, trying next...", model);
            } catch (Exception e) {
                log.error("Error with model {}: {}", model, e.getMessage());
                if (e.getMessage().contains("429")) {
                    return Map.of("error", "AI Quota Exceeded. Please wait 60 seconds and try again.");
                }
            }
        }

        return Map.of("error", "AI Service currently unavailable. Please try again later.");
    }

    public Map<String, Object> analyzeResume(String resumeText) {
        return analyzeResumeWithJD(resumeText, "General professional roles");
    }

    private Map<String, Object> callGeminiAPI(String model, String prompt) throws Exception {
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("${")) {
            throw new Exception("API Key Missing");
        }

        String url = BASE_URL + model + ":generateContent?key=" + apiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

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

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String text = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        return parseRobustJson(text);
    }

    private Map<String, Object> parseRobustJson(String text) {
        try {
            // Find the first '{' and last '}'
            int start = text.indexOf("{");
            int end = text.lastIndexOf("}");
            if (start != -1 && end != -1) {
                String jsonStr = text.substring(start, end + 1);
                return objectMapper.readValue(jsonStr, Map.class);
            }
            throw new Exception("No JSON found in response");
        } catch (Exception e) {
            log.error("JSON Parsing failed: {}", text);
            return Map.of("error", "Failed to parse AI response. Please try again.");
        }
    }
}
