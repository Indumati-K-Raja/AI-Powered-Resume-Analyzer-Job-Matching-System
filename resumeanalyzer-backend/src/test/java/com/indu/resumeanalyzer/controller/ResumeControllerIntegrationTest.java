package com.indu.resumeanalyzer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class ResumeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetHistory() throws Exception {
        mockMvc.perform(get("/resume/history"))
               .andExpect(status().isOk());
    }

    @Test
    public void testUploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/resume/analyze").file(emptyFile))
               .andExpect(status().isBadRequest());
    }
}
