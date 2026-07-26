package com.schoolexam.config;

import com.schoolexam.model.*;
import com.schoolexam.repository.*;
import com.schoolexam.service.BulkProcessingService;
import com.schoolexam.service.PaperSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private RubricRepository rubricRepository;

    @Autowired
    private LlmConfigRepository llmConfigRepository;

    @Autowired
    private PaperSubmissionService submissionService;

    @Autowired
    private BulkProcessingService bulkProcessingService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Demo Teacher User
        if (!userRepository.existsByUsername("teacher@school.edu")) {
            User teacher = User.builder()
                    .username("teacher@school.edu")
                    .password(passwordEncoder.encode("password123"))
                    .fullName("Prof. Eleanor Vance")
                    .role("TEACHER")
                    .build();
            userRepository.save(teacher);
        }

        // 2. Seed Free Tier LLM Configurations
        if (llmConfigRepository.count() == 0) {
            llmConfigRepository.saveAll(List.of(
                LlmConfig.builder().providerKey("gemini").displayName("Google Gemini 1.5 Flash").modelName("gemini-1.5-flash").isFreeTier(true).isDefault(true).build(),
                LlmConfig.builder().providerKey("gemini-2.5").displayName("Google Gemini 2.5 Flash").modelName("gemini-2.5-flash").isFreeTier(true).isDefault(false).build(),
                LlmConfig.builder().providerKey("groq").displayName("Groq Llama 3.2 Vision").modelName("llama-3.2-11b-vision").isFreeTier(true).isDefault(false).build(),
                LlmConfig.builder().providerKey("huggingface").displayName("Hugging Face Free Inference").modelName("meta-llama/Llama-3.2-11B-Vision").isFreeTier(true).isDefault(false).build(),
                LlmConfig.builder().providerKey("openrouter").displayName("OpenRouter Free Tier").modelName("google/gemini-flash-1.5-exp").isFreeTier(true).isDefault(false).build(),
                LlmConfig.builder().providerKey("local").displayName("Local OCR Rule Engine (Offline)").modelName("heuristic-ocr-v1").isFreeTier(true).isDefault(false).build()
            ));
        }

        // 3. Seed Sample Exams & Rubrics
        if (examRepository.count() == 0) {
            Exam mathExam = examRepository.save(Exam.builder()
                    .title("Grade 10 Mathematics Final Evaluation")
                    .subject("Mathematics")
                    .totalMarks(100.0)
                    .passPercentage(40.0)
                    .instructions("Answer all questions. Show step-by-step working for partial credit.")
                    .answerKey("Q1: x = 5. Q2: Area = 154 sq cm. Q3: f'(x) = 12x^2 - 10x + 11.")
                    .build());

            rubricRepository.saveAll(List.of(
                Rubric.builder().examId(mathExam.getId()).criteriaName("Mathematical Accuracy").maxScore(40.0).weightPercentage(40.0).description("Evaluates correctness of numeric solutions and algebraic steps.").build(),
                Rubric.builder().examId(mathExam.getId()).criteriaName("Problem Solving Steps").maxScore(35.0).weightPercentage(35.0).description("Assesses logical flow and intermediate derivations.").build(),
                Rubric.builder().examId(mathExam.getId()).criteriaName("Clarity & Presentation").maxScore(25.0).weightPercentage(25.0).description("Checks formula notation and legible handwritten presentation.").build()
            ));

            Exam scienceExam = examRepository.save(Exam.builder()
                    .title("Physics & Cellular Biology Midterm")
                    .subject("Science")
                    .totalMarks(100.0)
                    .passPercentage(40.0)
                    .instructions("Include chemical equations and physical laws where relevant.")
                    .answerKey("Photosynthesis equation: 6CO2 + 6H2O -> C6H12O6 + 6O2. Newton's 2nd law: F = m*a.")
                    .build());

            rubricRepository.saveAll(List.of(
                Rubric.builder().examId(scienceExam.getId()).criteriaName("Scientific Concepts").maxScore(50.0).weightPercentage(50.0).description("Understands biological processes and physics principles.").build(),
                Rubric.builder().examId(scienceExam.getId()).criteriaName("Equation Accuracy").maxScore(30.0).weightPercentage(30.0).description("Correct chemical formulas and unit notation.").build(),
                Rubric.builder().examId(scienceExam.getId()).criteriaName("Explanatory Detail").maxScore(20.0).weightPercentage(20.0).description("Provides clear narrative descriptions.").build()
            ));

            // Seed initial submissions & evaluations
            PaperSubmission sub1 = submissionService.submitPaper(mathExam.getId(), "Sophia Chen", "MATH-101", null, null);
            PaperSubmission sub2 = submissionService.submitPaper(mathExam.getId(), "Marcus Vance", "MATH-102", null, null);
            PaperSubmission sub3 = submissionService.submitPaper(scienceExam.getId(), "Emma Watson", "SCI-201", null, null);
        }
    }
}
