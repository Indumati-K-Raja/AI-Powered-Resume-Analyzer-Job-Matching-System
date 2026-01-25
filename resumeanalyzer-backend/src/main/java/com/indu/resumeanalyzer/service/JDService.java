package com.indu.resumeanalyzer.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JDService {

    // Calculate fit score: how many required skills are present in resume
    public int calculateFitScore(List<String> resumeSkills, List<String> jdSkills) {
        if (jdSkills.isEmpty()) return 0;

        int matchCount = 0;
        for (String skill : jdSkills) {
            if (resumeSkills.contains(skill)) {
                matchCount++;
            }
        }

        double score = ((double) matchCount / jdSkills.size()) * 100;
        return (int) Math.round(score);
    }
}
