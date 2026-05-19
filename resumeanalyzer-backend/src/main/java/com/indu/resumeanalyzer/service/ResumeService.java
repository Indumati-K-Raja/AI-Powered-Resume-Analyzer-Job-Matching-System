package com.indu.resumeanalyzer.service;

import com.indu.resumeanalyzer.dto.ResumeAnalysisResponse;
import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import com.indu.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.indu.resumeanalyzer.util.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final GeminiService geminiService;
    private final OpenRouterService openRouterService;
    private final ResumeAnalysisRepository repository;
    private final PdfTextExtractor pdfExtractor;

    public ResumeAnalysisResponse analyzeResume(MultipartFile file, String jd) {
        String fileName = file.getOriginalFilename();
        String text = pdfExtractor.extractText(file);

        Map<String, Object> aiResult;
        try {
            // Try OpenRouter (Llama 3.3) first
            aiResult = openRouterService.analyze(text, jd);
        } catch (Exception e) {
            log.warn("OpenRouter failed, falling back to Gemini: {}", e.getMessage());
            // Fallback to Gemini
            aiResult = geminiService.analyzeResumeWithJD(text, jd);
        }

        if (aiResult.containsKey("error")) {
            return ResumeAnalysisResponse.builder()
                    .error(aiResult.get("error").toString())
                    .build();
        }

        return mapToResponse(aiResult, fileName, text);
    }

    private ResumeAnalysisResponse mapToResponse(Map<String, Object> result, String fileName, String text) {
        List<Map<String, Object>> rawFixes = (List<Map<String, Object>>) result.get("exactFixes");
        List<com.indu.resumeanalyzer.dto.ExactFix> mappedFixes = null;
        
        if (rawFixes != null) {
            mappedFixes = rawFixes.stream().map(m -> {
                com.indu.resumeanalyzer.dto.ExactFix f = new com.indu.resumeanalyzer.dto.ExactFix();
                f.setTitle(String.valueOf(m.getOrDefault("title", "")));
                f.setLocation(String.valueOf(m.getOrDefault("location", "")));
                f.setType(String.valueOf(m.getOrDefault("type", "")));
                f.setOriginalText(String.valueOf(m.getOrDefault("originalText", "")));
                f.setNewText(String.valueOf(m.getOrDefault("newText", "")));
                f.setKeywords((List<String>) m.get("keywords"));
                return f;
            }).toList();
        }

        ResumeAnalysisResponse response = ResumeAnalysisResponse.builder()
                .fileName(fileName)
                .resumeScore(Double.parseDouble(result.getOrDefault("resumeScore", 0).toString()))
                .atsEval(String.valueOf(result.getOrDefault("atsEval", "N/A")))
                .recruiterEval(String.valueOf(result.getOrDefault("recruiterEval", "N/A")))
                .shortlistEval(String.valueOf(result.getOrDefault("shortlistEval", "N/A")))
                .verdict(String.valueOf(result.getOrDefault("verdict", "N/A")))
                .generalFeedback(String.valueOf(result.getOrDefault("generalFeedback", "")))
                .exactFixes(mappedFixes)
                .build();

        // Optional: Save to DB
        saveToDb(response, fileName, text);
        
        return response;
    }

    private void saveToDb(ResumeAnalysisResponse res, String fileName, String text) {
        try {
            ResumeAnalysis entity = new ResumeAnalysis();
            entity.setId(UUID.randomUUID());
            entity.setFileName(fileName);
            entity.setExtractedText(text);
            entity.setResumeScore(res.getResumeScore());
            entity.setCreatedAt(LocalDateTime.now());
            repository.save(entity);
        } catch (Exception e) {
            log.error("Failed to save analysis to DB: {}", e.getMessage());
        }
    }
}
