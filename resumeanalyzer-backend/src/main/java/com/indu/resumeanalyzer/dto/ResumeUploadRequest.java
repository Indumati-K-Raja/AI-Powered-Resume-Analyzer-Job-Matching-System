package com.indu.resumeanalyzer.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class ResumeUploadRequest {
    private MultipartFile file;
    private String jobDescription;
}
