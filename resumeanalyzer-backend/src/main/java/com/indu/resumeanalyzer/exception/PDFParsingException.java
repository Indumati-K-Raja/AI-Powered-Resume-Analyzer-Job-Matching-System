package com.indu.resumeanalyzer.exception;

public class PDFParsingException extends RuntimeException {
    public PDFParsingException(String message) {
        super(message);
    }
    public PDFParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
