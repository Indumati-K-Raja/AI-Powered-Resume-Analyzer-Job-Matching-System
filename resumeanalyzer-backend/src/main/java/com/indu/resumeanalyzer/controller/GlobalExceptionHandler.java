package com.indu.resumeanalyzer.controller;

import com.indu.resumeanalyzer.exception.PDFParsingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return buildErrorResponse("File too large. Max size is 5MB.", "ERR_FILE_SIZE", HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(PDFParsingException.class)
    public ResponseEntity<Map<String, Object>> handlePDFParsingException(PDFParsingException exc) {
        return buildErrorResponse(exc.getMessage(), "ERR_PDF_PARSE", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException exc) {
        return buildErrorResponse(exc.getMessage(), "ERR_VALIDATION", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception exc) {
        log.error("Global exception caught: ", exc);
        return buildErrorResponse("An unexpected error occurred: " + exc.getMessage(), "ERR_INTERNAL", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, String code, HttpStatus status) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("error", message);
        errorDetails.put("code", code);
        return new ResponseEntity<>(errorDetails, status);
    }
}
