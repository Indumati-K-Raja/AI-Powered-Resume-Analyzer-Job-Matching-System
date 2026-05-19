package com.indu.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class JobMatchResult {
    private double match_score;
    private List<String> matched_skills;
    private List<String> missing_skills;
    private List<String> extra_skills;
    private double match_percentage;
}
