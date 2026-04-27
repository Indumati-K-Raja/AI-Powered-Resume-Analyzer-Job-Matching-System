package com.indu.resumeanalyzer.util;

import com.indu.resumeanalyzer.exception.PDFParsingException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public class PdfTextExtractor {

    private final Tika tika = new Tika();

    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        try (InputStream inputStream = file.getInputStream()) {
            String text = tika.parseToString(inputStream);
            if (text == null || text.trim().isEmpty()) {
                throw new PDFParsingException("Could not extract any text from the document. It might be an image-based PDF or empty.");
            }
            return text;
        } catch (Exception e) {
            throw new PDFParsingException("Failed to parse the document: " + e.getMessage(), e);
        }
    }
}
