-- Test Data for Integration Tests
INSERT INTO resume_analyses (id, file_name, extracted_text, resume_score, job_fit_score, created_at, updated_at) 
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'test_resume.pdf', 'Java Developer Resume...', 85.0, 90.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
