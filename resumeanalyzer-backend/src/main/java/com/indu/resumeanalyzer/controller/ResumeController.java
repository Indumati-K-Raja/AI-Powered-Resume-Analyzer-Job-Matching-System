package com.indu.resumeanalyzer.controller;

import com.indu.resumeanalyzer.dto.ResumeAnalysisResponse;
import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import com.indu.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.indu.resumeanalyzer.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @RequestParam("file") MultipartFile file) {
        
        log.info("Analyze request for: {}", file.getOriginalFilename());
        ResumeAnalysisResponse response = resumeService.analyzeResume(file, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyzeWithJD")
    public ResponseEntity<ResumeAnalysisResponse> analyzeWithJD(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription) {
        
        log.info("AnalyzeWithJD request for: {}", file.getOriginalFilename());
        ResumeAnalysisResponse response = resumeService.analyzeResume(file, jobDescription);
        return ResponseEntity.ok(response);
    }
}
