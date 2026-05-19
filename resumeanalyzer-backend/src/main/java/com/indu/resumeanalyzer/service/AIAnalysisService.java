package com.indu.resumeanalyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indu.resumeanalyzer.dto.*;
import com.indu.resumeanalyzer.exception.AIServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Service
public class AIAnalysisService {
    
    @Value("${nlp.service.url:http://localhost:8001}")
    private String nlpServiceUrl;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AIAnalysisService.class);
    
    @Autowired
    public AIAnalysisService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        // We'll set the baseUrl in the builder to avoid issues with nlpServiceUrl being null during construction
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }
    
    private String getBaseUrl() {
        return nlpServiceUrl;
    }
    
    public SkillExtractionResult extractSkills(String resumeText) {
        try {
            Map<String, String> request = Map.of("resume_text", resumeText);
            
            String response = webClient.post()
                .uri(getBaseUrl() + "/extract-skills")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
            
            return objectMapper.readValue(response, SkillExtractionResult.class);
        } catch (Exception e) {
            logger.error("Error extracting skills from NLP service", e);
            throw new AIServiceException("Failed to extract skills", e);
        }
    }
    
    public ResumeScoreResult calculateResumeScore(String resumeText) {
        try {
            Map<String, String> request = Map.of("resume_text", resumeText);
            
            String response = webClient.post()
                .uri(getBaseUrl() + "/resume-score")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
            
            return objectMapper.readValue(response, ResumeScoreResult.class);
        } catch (Exception e) {
            logger.error("Error calculating resume score", e);
            throw new AIServiceException("Failed to calculate resume score", e);
        }
    }
    
    public JobMatchResult matchJobDescription(String resumeText, String jobDescription) {
        try {
            Map<String, String> request = Map.of(
                "resume_text", resumeText,
                "job_description", jobDescription
            );
            
            String response = webClient.post()
                .uri(getBaseUrl() + "/match-job-description")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
            
            return objectMapper.readValue(response, JobMatchResult.class);
        } catch (Exception e) {
            logger.error("Error matching job description", e);
            throw new AIServiceException("Failed to match job description", e);
        }
    }
    
    public ImprovementSuggestionsResult getSuggestions(String resumeText) {
        try {
            Map<String, String> request = Map.of("resume_text", resumeText);
            
            String response = webClient.post()
                .uri(getBaseUrl() + "/improvement-suggestions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
            
            return objectMapper.readValue(response, ImprovementSuggestionsResult.class);
        } catch (Exception e) {
            logger.error("Error getting improvement suggestions", e);
            throw new AIServiceException("Failed to get improvement suggestions", e);
        }
    }
}
