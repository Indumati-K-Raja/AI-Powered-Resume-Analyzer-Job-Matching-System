package com.indu.resumeanalyzer.controller;

import com.indu.resumeanalyzer.dto.*;
import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import com.indu.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.indu.resumeanalyzer.service.AIAnalysisService;
import com.indu.resumeanalyzer.util.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final AIAnalysisService aiService;
    private final ResumeAnalysisRepository repository;
    private final PdfTextExtractor pdfExtractor;

    @GetMapping("/history")
    public ResponseEntity<List<ResumeAnalysis>> getHistory() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(@RequestParam("file") MultipartFile file) {
        try {
            validatePdfFile(file);
            String extractedText = pdfExtractor.extractText(file);
            
            // Call AI service for NLP analysis
            SkillExtractionResult skills = aiService.extractSkills(extractedText);
            ResumeScoreResult score = aiService.calculateResumeScore(extractedText);
            ImprovementSuggestionsResult suggestions = aiService.getSuggestions(extractedText);
            
            // Build response
            ResumeAnalysisResponse response = new ResumeAnalysisResponse();
            response.setFileName(file.getOriginalFilename());
            response.setExtractedSkills(skills.getDetected_skills());
            response.setResumeScore(score.getOverall());
            response.setSuggestions(suggestions.getSuggestions());
            response.setScoreBreakdown(score);
            
            // Save to database
            ResumeAnalysis analysis = new ResumeAnalysis();
            analysis.setFileName(file.getOriginalFilename());
            analysis.setExtractedText(extractedText);
            analysis.setDetectedSkills(new HashSet<>(skills.getDetected_skills()));
            analysis.setResumeScore(score.getOverall());
            repository.save(analysis);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error analyzing resume", e);
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/analyzeWithJD")
    public ResponseEntity<?> analyzeWithJobDescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription) {
        try {
            validatePdfFile(file);
            String extractedText = pdfExtractor.extractText(file);
            
            // Get AI analysis
            SkillExtractionResult skills = aiService.extractSkills(extractedText);
            ResumeScoreResult score = aiService.calculateResumeScore(extractedText);
            JobMatchResult jobMatch = aiService.matchJobDescription(extractedText, jobDescription);
            
            // Build comprehensive response
            ResumeAnalysisResponse response = new ResumeAnalysisResponse();
            response.setFileName(file.getOriginalFilename());
            response.setExtractedSkills(skills.getDetected_skills());
            response.setMissingSkills(jobMatch.getMissing_skills());
            response.setResumeScore(score.getOverall());
            response.setJdMatchScore(jobMatch.getMatch_score());
            response.setScoreBreakdown(score);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error analyzing resume with JD", e);
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    private void validatePdfFile(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File is empty");
        if (!"application/pdf".equals(file.getContentType())) 
            throw new RuntimeException("Only PDF files allowed");
        if (file.getSize() > 5_000_000) 
            throw new RuntimeException("File size exceeds 5MB limit");
    }
}
