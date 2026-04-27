package com.indu.resumeanalyzer.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisResponse {
    private String fileName;
    private double resumeScore;
    private double jdMatchScore;
    
    // New rich schema fields
    private String atsEval;
    private String recruiterEval;
    private String shortlistEval;
    private String verdict;
    private String generalFeedback;
    private List<ExactFix> exactFixes;
    
    // Legacy fields (optional)
    private List<String> extractedSkills;
    private List<String> missingSkills;
    private List<String> suggestions;
}
