package com.indu.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ImprovementSuggestionsResult {
    @Data
    public static class Suggestion {
        private String category;
        private String severity; // high, medium, low
        private String message;
        private String impact;
    }
    
    private List<Suggestion> suggestions;
    private int estimated_improvement;
}
