package com.indu.resumeanalyzer.controller;

import com.indu.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.indu.resumeanalyzer.service.skillService;
import com.indu.resumeanalyzer.service.JDService;
import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    @Autowired
    private skillService skillService;

    @Autowired
    private JDService jdService;

    @Autowired
    private ResumeAnalysisRepository repository;

    // 1️⃣ Upload resume
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty())
            return ResponseEntity.badRequest().body("File is empty");

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            return ResponseEntity.ok(text.substring(0, Math.min(500, text.length())));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error processing resume: " + e.getMessage());
        }
    }

    // 2️⃣ Analyze resume (NO JD)
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeResume(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty())
            return ResponseEntity.badRequest().body("File is empty");

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String resumeText = stripper.getText(document);

            List<String> skillsFound = skillService.extractSkills(resumeText);
            int score = skillService.calculateResumeScore(resumeText, skillsFound);

            // ✅ Save to DB
            ResumeAnalysis analysis = new ResumeAnalysis();
            analysis.setFileName(file.getOriginalFilename());
            analysis.setSkills(skillsFound);
            analysis.setResumeScore(score);

            repository.save(analysis);

            return ResponseEntity.ok(
                    Map.of(
                            "skills_found", skillsFound,
                            "score", score
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error processing resume: " + e.getMessage());
        }
    }

    // 3️⃣ Analyze resume WITH Job Description
    @PostMapping(value = "/analyzeWithJD", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeWithJD(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jd") String jobDescription
    ) {

        if (file.isEmpty())
            return ResponseEntity.badRequest().body("File is empty");

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String resumeText = stripper.getText(document);

            List<String> resumeSkills = skillService.extractSkills(resumeText);
            List<String> jdSkills = skillService.extractSkills(jobDescription);
            List<String> missingSkills =
                    skillService.findMissingSkills(resumeSkills, jdSkills);

            int resumeScore = skillService.calculateResumeScore(resumeText, resumeSkills);
            int jobFitScore = jdService.calculateFitScore(resumeSkills, jdSkills);

            // ✅ Save to DB
            ResumeAnalysis analysis = new ResumeAnalysis();
            analysis.setFileName(file.getOriginalFilename());
            analysis.setSkills(resumeSkills);
            analysis.setMissingSkills(missingSkills);
            analysis.setResumeScore(resumeScore);
            analysis.setJobFitScore(jobFitScore);

            repository.save(analysis);

            return ResponseEntity.ok(
                    Map.of(
                            "resume_skills", resumeSkills,
                            "missing_skills", missingSkills,
                            "resume_score", resumeScore,
                            "job_fit_score", jobFitScore
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error processing resume: " + e.getMessage());
        }
    }
}
