package com.indu.resumeanalyzer.repository;

import com.indu.resumeanalyzer.entity.ResumeAnalysis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("dev")
public class ResumeAnalysisRepositoryTest {

    @Autowired
    private ResumeAnalysisRepository repository;

    @Test
    public void testSaveAndFindAll() {
        ResumeAnalysis analysis = ResumeAnalysis.builder()
                .fileName("test.pdf")
                .extractedText("some text")
                .resumeScore(80)
                .build();
        
        repository.save(analysis);

        List<ResumeAnalysis> results = repository.findAll();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getFileName()).isEqualTo("test.pdf");
    }
}
