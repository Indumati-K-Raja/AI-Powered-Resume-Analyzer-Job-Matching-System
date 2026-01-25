package com.indu.resumeanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SpringBootApplication
@Repository

public class ResumeanalyzerApplication {

	public static void main(String[] args) {

		SpringApplication.run(ResumeanalyzerApplication.class, args);
	}
	public interface ResumeAnalysisRepository
			extends JpaRepository<ResumeAnalysis, Long> {
	}

}
