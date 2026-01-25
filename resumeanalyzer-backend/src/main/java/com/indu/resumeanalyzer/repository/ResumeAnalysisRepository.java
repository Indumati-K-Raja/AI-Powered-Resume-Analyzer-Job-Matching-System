package com.indu.resumeanalyzer.repository;

import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeAnalysisRepository
        extends JpaRepository<ResumeAnalysis, Long> {
}
