package com.schoolexam.service;

import com.schoolexam.dto.EvaluationDtos.*;
import com.schoolexam.model.*;
import com.schoolexam.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LlmEvaluationServiceTest {

    @Mock
    private PaperSubmissionRepository submissionRepository;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private RubricRepository rubricRepository;

    @Mock
    private EvaluationResultRepository resultRepository;

    @Mock
    private LlmConfigRepository llmConfigRepository;

    @InjectMocks
    private LlmEvaluationService evaluationService;

    private PaperSubmission sampleSubmission;
    private Exam sampleExam;
    private List<Rubric> sampleRubrics;

    @BeforeEach
    void setUp() {
        sampleExam = Exam.builder()
                .id(10L)
                .title("Mathematics Exam")
                .subject("Mathematics")
                .totalMarks(100.0)
                .passPercentage(40.0)
                .build();

        sampleSubmission = PaperSubmission.builder()
                .id(1L)
                .examId(10L)
                .studentName("Sophia Chen")
                .rollNumber("MATH-101")
                .ocrText("Q1. Solve 3x+7=22. Student Answer: x=5.")
                .status("PENDING")
                .build();

        sampleRubrics = List.of(
                Rubric.builder().id(1L).examId(10L).criteriaName("Accuracy").maxScore(50.0).weightPercentage(50.0).build(),
                Rubric.builder().id(2L).examId(10L).criteriaName("Reasoning").maxScore(50.0).weightPercentage(50.0).build()
        );
    }

    @Test
    void testEvaluatePaper_Success() {
        EvaluationRequest request = new EvaluationRequest();
        request.setSubmissionId(1L);
        request.setProviderKey("gemini");
        request.setModelName("gemini-1.5-flash");

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(sampleSubmission));
        when(examRepository.findById(10L)).thenReturn(Optional.of(sampleExam));
        when(rubricRepository.findByExamId(10L)).thenReturn(sampleRubrics);
        when(resultRepository.findBySubmissionId(1L)).thenReturn(Optional.empty());
        when(resultRepository.save(any())).thenAnswer(i -> {
            EvaluationResult res = i.getArgument(0);
            res.setId(99L);
            return res;
        });

        EvaluationDetailDto dto = evaluationService.evaluatePaper(request);

        assertNotNull(dto);
        assertEquals("Sophia Chen", dto.getStudentName());
        assertEquals("Mathematics Exam", dto.getExamTitle());
        assertEquals("PASSED", dto.getIsPassed());
        assertTrue(dto.getTotalMarksObtained() > 0);
        assertEquals(2, dto.getRubricBreakdown().size());
        assertNotNull(dto.getDetailedFeedback());
        verify(submissionRepository, times(1)).save(any());
    }
}
