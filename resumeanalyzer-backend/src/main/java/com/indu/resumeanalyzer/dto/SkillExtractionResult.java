package com.indu.resumeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillExtractionResult {
    private List<String> detected_skills;
    private Map<String, Double> skill_confidence;
    private Map<String, List<String>> entities;
    private List<String> certifications;
}
