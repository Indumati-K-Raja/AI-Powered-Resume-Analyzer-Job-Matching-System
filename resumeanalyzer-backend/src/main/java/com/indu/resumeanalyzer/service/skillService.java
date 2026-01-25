package com.indu.resumeanalyzer.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class skillService {

    private final Map<String, List<String>> skillsMap = Map.ofEntries(
            Map.entry("Java", List.of("java")),
            Map.entry("Spring Boot", List.of("spring boot", "spring")),
            Map.entry("React", List.of("react", "react.js", "reactjs")),
            Map.entry("Node.js", List.of("node", "node.js", "nodejs")),
            Map.entry("Python", List.of("python")),
            Map.entry("SQL", List.of("sql", "mysql", "postgresql", "oracle")),
            Map.entry("JavaScript", List.of("javascript", "js")),
            Map.entry("HTML", List.of("html")),
            Map.entry("CSS", List.of("css")),
            Map.entry("NLP", List.of("nlp", "natural language processing")),
            Map.entry("Leadership", List.of("leadership", "team lead")),
            Map.entry("Communication", List.of("communication", "communicate", "speaking")),
            Map.entry("Teamwork", List.of("teamwork", "team player"))
    );

    // ✅ Extract skills
    public List<String> extractSkills(String text) {
        Set<String> found = new HashSet<>();
        String lower = text.toLowerCase();

        for (Map.Entry<String, List<String>> entry : skillsMap.entrySet()) {
            for (String synonym : entry.getValue()) {
                if (lower.contains(synonym)) {
                    found.add(entry.getKey());
                    break;
                }
            }
        }
        return new ArrayList<>(found);
    }


    // ✅ Resume-only score
    public int calculateResumeScore(String resumeText, List<String> skills) {
        int skillScore = Math.min(40, skills.size() * 6);

        int sectionScore = sectionScore(resumeText);
        int actionScore = actionVerbScore(resumeText);
        int impactScore = quantifiedImpactScore(resumeText);
        int lengthScore = lengthScore(resumeText);

        return Math.min(100,
                skillScore + sectionScore + actionScore + impactScore + lengthScore
        );
    }


    // ✅ Resume vs JD score
    public int calculateMatchScore(List<String> resumeSkills, List<String> jdSkills) {
        if (resumeSkills.isEmpty() || jdSkills.isEmpty()) return 0;

        long matchCount = jdSkills.stream()
                .filter(resumeSkills::contains)
                .count();

        return (int) Math.round((matchCount * 100.0) / jdSkills.size());
    }

    // ✅ Missing skills
    public List<String> findMissingSkills(List<String> resumeSkills, List<String> jdSkills) {
        List<String> missing = new ArrayList<>();
        for (String skill : jdSkills) {
            if (!resumeSkills.contains(skill)) {
                missing.add(skill);
            }
        }
        return missing;

    }

    public int sectionScore(String text) {
        int score = 0;
        String lower = text.toLowerCase();

        if (lower.contains("summary")  ||  lower.contains("objective") || lower.contains("profile")) score += 6;
        if (lower.contains("skills") ||  lower.contains("technical skills")) score += 6;
        if (lower.contains("experience") || lower.contains("projects")) score += 7;
        if (lower.contains("education")) score += 6;

        return score; // max 25
    }

    private final List<String> actionVerbs = List.of(
            "developed", "designed", "implemented", "built",
            "created", "led", "managed", "optimized",
            "improved", "analyzed", "automated"
    );
    public int actionVerbScore(String text) {
        String lower = text.toLowerCase();
        long count = actionVerbs.stream()
                .filter(lower::contains)
                .count();

        return (int) Math.min(15, count * 3);
    }

    public int quantifiedImpactScore(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                count++;
                break;
            }
        }
        return count > 0 ? 10 : 0;
    }

    public int lengthScore(String text) {
        int words = text.split("\\s+").length;

        if (words >= 300 && words <= 800) return 10;
        if (words >= 200) return 6;
        return 3;
    }

    public Map<String, String> getSkillSuggestions(List<String> missingSkills) {

        Map<String, String> suggestions = new HashMap<>();

        for (String skill : missingSkills) {
            switch (skill) {
                case "Java":
                    suggestions.put(skill, "Improve OOP concepts and practice Spring Boot projects");
                    break;
                case "Spring Boot":
                    suggestions.put(skill, "Learn REST APIs, JPA, and Spring Security");
                    break;
                case "React":
                    suggestions.put(skill, "Learn components, hooks, props, and state management");
                    break;
                case "Python":
                    suggestions.put(skill, "Start with basics, then focus on scripting and APIs");
                    break;
                case "SQL":
                    suggestions.put(skill, "Practice joins, subqueries, and database design");
                    break;
                case "Communication":
                    suggestions.put(skill, "Improve presentation and interview communication skills");
                    break;
                default:
                    suggestions.put(skill, "Learn fundamentals and practice hands-on projects");
            }
        }
        return suggestions;
    }


}
