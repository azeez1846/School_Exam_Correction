package com.schoolexam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolexam.dto.EvaluationDtos.*;
import com.schoolexam.model.*;
import com.schoolexam.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private PaperSubmissionRepository submissionRepository;

    @Mock
    private EvaluationResultRepository resultRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AnalyticsService analyticsService;

    private Exam sampleExam;
    private PaperSubmission sampleSubmission;
    private EvaluationResult sampleResult;

    @BeforeEach
    void setUp() throws Exception {
        sampleExam = Exam.builder()
                .id(10L)
                .title("Physics Midterm")
                .subject("Physics")
                .totalMarks(100.0)
                .build();

        sampleSubmission = PaperSubmission.builder()
                .id(1L)
                .examId(10L)
                .studentName("Alex Rivera")
                .rollNumber("PHY-101")
                .build();

        List<RubricItemScore> breakdown = List.of(
                new RubricItemScore("Accuracy", 40.0, 50.0, "Good"),
                new RubricItemScore("Derivations", 45.0, 50.0, "Very clear")
        );

        sampleResult = EvaluationResult.builder()
                .id(100L)
                .submissionId(1L)
                .totalMarksObtained(85.0)
                .maxMarks(100.0)
                .percentageScore(85.0)
                .grade("A")
                .isPassed("PASSED")
                .rubricBreakdownJson(objectMapper.writeValueAsString(breakdown))
                .improvementsJson(objectMapper.writeValueAsString(List.of("Include SI units in final answers", "Show step by step vector components")))
                .build();
    }

    @Test
    void testGetDashboardStats() {
        when(examRepository.count()).thenReturn(1L);
        when(submissionRepository.count()).thenReturn(1L);
        when(resultRepository.findAll()).thenReturn(List.of(sampleResult));

        DashboardStats stats = analyticsService.getDashboardStats();
        assertNotNull(stats);
        assertEquals(1, stats.getTotalExams());
        assertEquals(85.0, stats.getClassAveragePercentage());
    }

    @Test
    void testGetItemAnalysisForExam() {
        when(examRepository.findById(10L)).thenReturn(Optional.of(sampleExam));
        when(submissionRepository.findByExamId(10L)).thenReturn(List.of(sampleSubmission));
        when(resultRepository.findBySubmissionId(1L)).thenReturn(Optional.of(sampleResult));

        ItemAnalysisDto dto = analyticsService.getItemAnalysisForExam(10L);

        assertNotNull(dto);
        assertEquals(10L, dto.getExamId());
        assertEquals("Physics Midterm", dto.getExamTitle());
        assertEquals(1, dto.getTotalPapersEvaluated());
        assertEquals(85.0, dto.getClassAveragePercentage());
        assertEquals(2, dto.getRubricItemStats().size());
    }

    @Test
    void testGetMisconceptionClustersForExam() {
        when(examRepository.findById(10L)).thenReturn(Optional.of(sampleExam));
        when(submissionRepository.findByExamId(10L)).thenReturn(List.of(sampleSubmission));
        when(resultRepository.findBySubmissionId(1L)).thenReturn(Optional.of(sampleResult));

        MisconceptionClusterDto dto = analyticsService.getMisconceptionClustersForExam(10L);

        assertNotNull(dto);
        assertEquals(10L, dto.getExamId());
        assertEquals(2, dto.getCommonErrors().size());
        assertTrue(dto.getCommonErrors().get(0).getRecommendation().contains("Review foundational concepts"));
    }
}
