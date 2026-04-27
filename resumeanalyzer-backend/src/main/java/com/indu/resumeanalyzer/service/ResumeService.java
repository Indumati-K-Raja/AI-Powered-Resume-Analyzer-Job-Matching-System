package com.indu.resumeanalyzer.service;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final PdfTextExtractor textExtractor;
    private final GeminiService geminiService;
    private final ResumeAnalysisRepository repository;

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

        List<String> resumeSkills = extractListFromMap(geminiResult, hasJd ? "resume_skills" : "skills_found");
        List<String> missingSkills = hasJd ? extractListFromMap(geminiResult, "missing_skills") : new ArrayList<>();
        int resumeScore = extractIntFromMap(geminiResult, hasJd ? "resume_score" : "score");
        int jobFitScore = hasJd ? extractIntFromMap(geminiResult, "job_fit_score") : 0;
        
        Map<String, String> suggestionsMap = (Map<String, String>) geminiResult.get("suggestions");
        List<String> suggestions = new ArrayList<>();
        if (suggestionsMap != null) {
            suggestions.addAll(suggestionsMap.values());
        }

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setFileName(file.getOriginalFilename());
        analysis.setExtractedText(resumeText);
        analysis.setDetectedSkills(new HashSet<>(resumeSkills));
        analysis.setMissingSkills(missingSkills);
        analysis.setResumeScore(resumeScore);
        analysis.setJobFitScore(jobFitScore);
        
        repository.save(analysis);
        log.info("Resume analysis saved to database with ID: {}", analysis.getId());

        return ResumeAnalysisResponse.builder()
                .fileName(file.getOriginalFilename())
                .resumeScore(resumeScore)
                .jdMatchScore(jobFitScore)
                .extractedSkills(resumeSkills)
                .missingSkills(missingSkills)
                .suggestions(suggestions)
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
}
