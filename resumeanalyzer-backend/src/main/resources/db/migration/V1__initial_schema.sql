CREATE TABLE IF NOT EXISTS resume_analyses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  file_name VARCHAR(255) NOT NULL,
  extracted_text TEXT NOT NULL,
  detected_skills TEXT[],
  resume_score DECIMAL(5, 2) NOT NULL,
  job_fit_score DECIMAL(5, 2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_created_at ON resume_analyses(created_at);

CREATE TABLE IF NOT EXISTS job_descriptions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(255) NOT NULL,
  company VARCHAR(255),
  description TEXT NOT NULL,
  required_skills TEXT[],
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
