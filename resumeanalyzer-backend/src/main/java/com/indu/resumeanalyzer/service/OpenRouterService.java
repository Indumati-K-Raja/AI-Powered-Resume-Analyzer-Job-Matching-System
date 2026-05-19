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
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenRouterService {

    @Value("${openrouter.api-key}")
    private String apiKey;

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final String MODEL = "meta-llama/llama-3.3-70b-instruct";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyze(String resumeText, String jdText) throws Exception {
        log.info("Analyzing with OpenRouter (Llama 3.3)...");

        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("${")) {
            throw new Exception("OpenRouter API Key Missing");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080"); // Required by OpenRouter
        headers.set("X-Title", "Resume Analyzer");

        String prompt = "You are an expert ATS and recruiter. Analyze this Resume against the Job Description.\n\n" +
                "RESUME:\n" + resumeText + "\n\n" +
                "JD:\n" + jdText + "\n\n" +
                "Return ONLY a JSON object: {resumeScore (0-100), atsEval, recruiterEval, shortlistEval, verdict, generalFeedback, exactFixes:[{title, location, type, originalText, newText, keywords}]}";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String text = rootNode.path("choices").get(0).path("message").path("content").asText();

        return parseRobustJson(text);
    }

    private Map<String, Object> parseRobustJson(String text) {
        try {
            int start = text.indexOf("{");
            int end = text.lastIndexOf("}");
            if (start != -1 && end != -1) {
                String jsonStr = text.substring(start, end + 1);
                return objectMapper.readValue(jsonStr, Map.class);
            }
            throw new Exception("No JSON found");
        } catch (Exception e) {
            log.error("OpenRouter JSON Parse Error: {}", text);
            return Map.of("error", "AI sent malformed data. Please try again.");
        }
    }
}
