package com.schoolexam.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class OcrServiceTest {

    private final OcrService ocrService = new OcrService();

    @Test
    void testExtractText_WithManualOverride() throws IOException {
        String override = "Custom teacher handwritten text";
        String extracted = ocrService.extractTextFromPaper(null, override);
        assertEquals(override, extracted);
    }

    @Test
    void testExtractText_WithTextFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "paper.txt", "text/plain", "Q1: Answer is 42.".getBytes());
        String extracted = ocrService.extractTextFromPaper(file, null);
        assertEquals("Q1: Answer is 42.", extracted);
    }

    @Test
    void testExtractText_WithImageSimulation() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "math_paper.jpg", "image/jpeg", new byte[]{1, 2, 3});
        String extracted = ocrService.extractTextFromPaper(file, null);
        assertNotNull(extracted);
        assertTrue(extracted.contains("Mathematics"));
    }
}
