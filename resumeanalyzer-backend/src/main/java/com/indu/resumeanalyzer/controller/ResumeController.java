package com.indu.resumeanalyzer.controller;

import com.indu.resumeanalyzer.dto.*;
import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import com.indu.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.indu.resumeanalyzer.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeAnalysisRepository repository;

    @GetMapping("/history")
    public ResponseEntity<List<ResumeAnalysis>> getHistory() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(@RequestParam("file") MultipartFile file) {
        try {
            validatePdfFile(file);
            
            // Delegate complete analysis to ResumeService (OpenRouter/Gemini)
            ResumeAnalysisResponse response = resumeService.analyzeResume(file, null);
            
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
            
            // Delegate complete analysis to ResumeService (OpenRouter/Gemini)
            ResumeAnalysisResponse response = resumeService.analyzeResume(file, jobDescription);
            
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
