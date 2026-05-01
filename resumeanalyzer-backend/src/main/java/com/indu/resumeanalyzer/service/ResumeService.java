package com.indu.resumeanalyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indu.resumeanalyzer.dto.ExactFix;
import com.indu.resumeanalyzer.dto.ResumeAnalysisResponse;
import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import com.indu.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.indu.resumeanalyzer.util.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final PdfTextExtractor textExtractor;
    private final GeminiService geminiService;
    private final ResumeAnalysisRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public ResumeAnalysisResponse analyzeResume(MultipartFile file, String jobDescription) {
        log.info("Starting resume analysis for file: {}", file.getOriginalFilename());
        
        String resumeText = textExtractor.extractText(file);
        
        Map<String, Object> geminiResult;
        boolean hasJd = jobDescription != null && !jobDescription.trim().isEmpty();
        
        if (hasJd) {
            geminiResult = geminiService.analyzeResumeWithJD(resumeText, jobDescription);
        } else {
            geminiResult = geminiService.analyzeResume(resumeText);
        }

        if (geminiResult.containsKey("error")) {
            throw new RuntimeException((String) geminiResult.get("error"));
        }

        // Extract metrics using camelCase keys to match GeminiService prompt
        int matchScore = extractIntFromMap(geminiResult, "resumeScore");
        
        String atsEval = extractStringFromMap(geminiResult, "atsEval");
        String recruiterEval = extractStringFromMap(geminiResult, "recruiterEval");
        String shortlistEval = extractStringFromMap(geminiResult, "shortlistEval");
        String verdict = extractStringFromMap(geminiResult, "verdict");
        String generalFeedback = extractStringFromMap(geminiResult, "generalFeedback");

        List<ExactFix> exactFixes = new ArrayList<>();
        Object fixesObj = geminiResult.get("exactFixes");
        if (fixesObj instanceof List) {
            List<Map<String, Object>> fixesList = (List<Map<String, Object>>) fixesObj;
            for (Map<String, Object> fixMap : fixesList) {
                ExactFix fix = ExactFix.builder()
                        .title(extractStringFromMap(fixMap, "title"))
                        .location(extractStringFromMap(fixMap, "location"))
                        .type(extractStringFromMap(fixMap, "type"))
                        .originalText(extractStringFromMap(fixMap, "originalText"))
                        .newText(extractStringFromMap(fixMap, "newText"))
                        .keywords(extractListFromMap(fixMap, "keywords"))
                        .build();
                exactFixes.add(fix);
            }
        }

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setFileName(file.getOriginalFilename());
        analysis.setExtractedText(resumeText);
        analysis.setResumeScore(matchScore);
        analysis.setJobFitScore(hasJd ? matchScore : 0);
        
        repository.save(analysis);
        log.info("Resume analysis saved to database with ID: {}", analysis.getId());

        return ResumeAnalysisResponse.builder()
                .fileName(file.getOriginalFilename())
                .resumeScore(matchScore)
                .jdMatchScore(hasJd ? matchScore : 0)
                .atsEval(atsEval != null ? atsEval : "Unknown")
                .recruiterEval(recruiterEval != null ? recruiterEval : "Unknown")
                .shortlistEval(shortlistEval != null ? shortlistEval : "Unknown")
                .verdict(verdict != null ? verdict : "Unknown")
                .generalFeedback(generalFeedback != null ? generalFeedback : "No feedback provided.")
                .exactFixes(exactFixes)
                .build();
    }

    private List<String> extractListFromMap(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if (obj instanceof List) {
            return (List<String>) obj;
        }
        return new ArrayList<>();
    }

    private int extractIntFromMap(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if (obj != null) {
            try {
                return Integer.parseInt(String.valueOf(obj));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    private String extractStringFromMap(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if (obj != null) {
            return String.valueOf(obj);
        }
        return null;
    }
}
