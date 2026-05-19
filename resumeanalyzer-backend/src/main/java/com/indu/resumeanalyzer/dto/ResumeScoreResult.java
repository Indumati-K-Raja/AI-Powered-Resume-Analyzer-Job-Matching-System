package com.indu.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResumeScoreResult {
    private double section_completeness;
    private double action_verbs;
    private double quantification;
    private double skill_density;
    private double overall;
}
