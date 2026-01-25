package com.indu.resumeanalyzer.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @ElementCollection
    private List<String> skills;

    @ElementCollection
    private List<String> missingSkills;

    private int resumeScore;

    private int jobFitScore;

    // 🔹 Getters & Setters

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public int getResumeScore() {
        return resumeScore;
    }

    public void setResumeScore(int resumeScore) {
        this.resumeScore = resumeScore;
    }

    public int getJobFitScore() {
        return jobFitScore;
    }

    public void setJobFitScore(int jobFitScore) {
        this.jobFitScore = jobFitScore;
    }
}
