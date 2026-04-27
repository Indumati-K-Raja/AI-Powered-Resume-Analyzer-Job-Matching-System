package com.indu.resumeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExactFix {
    private String title;
    private String location;
    private String type; // replace, add
    private String originalText;
    private String newText;
    private List<String> keywords;
}
