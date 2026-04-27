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
    private List<String> extractedSkills;
    private List<String> missingSkills;
    private List<String> suggestions;
    private double jdMatchScore;
}
