package com.indu.resumeanalyzer.controller;

import com.indu.resumeanalyzer.dto.ResumeAnalysisResponse;
import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import com.indu.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.indu.resumeanalyzer.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(@ModelAttribute com.indu.resumeanalyzer.dto.ResumeUploadRequest request) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        ResumeAnalysisResponse response = resumeService.analyzeResume(request.getFile(), null);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/analyzeWithJD", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeAnalysisResponse> analyzeWithJD(@ModelAttribute com.indu.resumeanalyzer.dto.ResumeUploadRequest request) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        ResumeAnalysisResponse response = resumeService.analyzeResume(request.getFile(), request.getJobDescription());
        return ResponseEntity.ok(response);
    }
}
