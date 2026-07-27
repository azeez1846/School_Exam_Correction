package com.schoolexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_results")
public class EvaluationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long submissionId;

    private Double totalMarksObtained;
    private Double maxMarks;
    private Double percentageScore;
    private String grade;
    private String isPassed; // PASSED, FAILED

    private String providerUsed;
    private String modelUsed;

    @Column(length = 4000)
    private String rubricBreakdownJson; // JSON array of criterion scores

    @Column(length = 2000)
    private String strengthsJson; // JSON array of key strengths

    @Column(length = 2000)
    private String improvementsJson; // JSON array of improvement areas

    @Column(length = 4000)
    private String detailedFeedback; // Custom written paragraph feedback for student

    private LocalDateTime evaluatedAt;

    private Boolean isTeacherOverridden = false;

    @Column(length = 2000)
    private String teacherNotes;

    public EvaluationResult() {}

    public EvaluationResult(Long id, Long submissionId, Double totalMarksObtained, Double maxMarks, Double percentageScore, String grade, String isPassed, String providerUsed, String modelUsed, String rubricBreakdownJson, String strengthsJson, String improvementsJson, String detailedFeedback, LocalDateTime evaluatedAt) {
        this(id, submissionId, totalMarksObtained, maxMarks, percentageScore, grade, isPassed, providerUsed, modelUsed, rubricBreakdownJson, strengthsJson, improvementsJson, detailedFeedback, evaluatedAt, false, null);
    }

    public EvaluationResult(Long id, Long submissionId, Double totalMarksObtained, Double maxMarks, Double percentageScore, String grade, String isPassed, String providerUsed, String modelUsed, String rubricBreakdownJson, String strengthsJson, String improvementsJson, String detailedFeedback, LocalDateTime evaluatedAt, Boolean isTeacherOverridden, String teacherNotes) {
        this.id = id;
        this.submissionId = submissionId;
        this.totalMarksObtained = totalMarksObtained;
        this.maxMarks = maxMarks;
        this.percentageScore = percentageScore;
        this.grade = grade;
        this.isPassed = isPassed;
        this.providerUsed = providerUsed;
        this.modelUsed = modelUsed;
        this.rubricBreakdownJson = rubricBreakdownJson;
        this.strengthsJson = strengthsJson;
        this.improvementsJson = improvementsJson;
        this.detailedFeedback = detailedFeedback;
        this.evaluatedAt = evaluatedAt;
        this.isTeacherOverridden = isTeacherOverridden != null ? isTeacherOverridden : false;
        this.teacherNotes = teacherNotes;
    }

    @PrePersist
    protected void onCreate() {
        if (evaluatedAt == null) {
            evaluatedAt = LocalDateTime.now();
        }
    }

    public static EvaluationResultBuilder builder() {
        return new EvaluationResultBuilder();
    }

    public static class EvaluationResultBuilder {
        private Long id;
        private Long submissionId;
        private Double totalMarksObtained;
        private Double maxMarks;
        private Double percentageScore;
        private String grade;
        private String isPassed;
        private String providerUsed;
        private String modelUsed;
        private String rubricBreakdownJson;
        private String strengthsJson;
        private String improvementsJson;
        private String detailedFeedback;
        private LocalDateTime evaluatedAt;
        private Boolean isTeacherOverridden = false;
        private String teacherNotes;

        public EvaluationResultBuilder id(Long id) { this.id = id; return this; }
        public EvaluationResultBuilder submissionId(Long submissionId) { this.submissionId = submissionId; return this; }
        public EvaluationResultBuilder totalMarksObtained(Double totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; return this; }
        public EvaluationResultBuilder maxMarks(Double maxMarks) { this.maxMarks = maxMarks; return this; }
        public EvaluationResultBuilder percentageScore(Double percentageScore) { this.percentageScore = percentageScore; return this; }
        public EvaluationResultBuilder grade(String grade) { this.grade = grade; return this; }
        public EvaluationResultBuilder isPassed(String isPassed) { this.isPassed = isPassed; return this; }
        public EvaluationResultBuilder providerUsed(String providerUsed) { this.providerUsed = providerUsed; return this; }
        public EvaluationResultBuilder modelUsed(String modelUsed) { this.modelUsed = modelUsed; return this; }
        public EvaluationResultBuilder rubricBreakdownJson(String rubricBreakdownJson) { this.rubricBreakdownJson = rubricBreakdownJson; return this; }
        public EvaluationResultBuilder strengthsJson(String strengthsJson) { this.strengthsJson = strengthsJson; return this; }
        public EvaluationResultBuilder improvementsJson(String improvementsJson) { this.improvementsJson = improvementsJson; return this; }
        public EvaluationResultBuilder detailedFeedback(String detailedFeedback) { this.detailedFeedback = detailedFeedback; return this; }
        public EvaluationResultBuilder evaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; return this; }
        public EvaluationResultBuilder isTeacherOverridden(Boolean isTeacherOverridden) { this.isTeacherOverridden = isTeacherOverridden; return this; }
        public EvaluationResultBuilder teacherNotes(String teacherNotes) { this.teacherNotes = teacherNotes; return this; }

        public EvaluationResult build() {
            return new EvaluationResult(id, submissionId, totalMarksObtained, maxMarks, percentageScore, grade, isPassed, providerUsed, modelUsed, rubricBreakdownJson, strengthsJson, improvementsJson, detailedFeedback, evaluatedAt, isTeacherOverridden, teacherNotes);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

    public Double getTotalMarksObtained() { return totalMarksObtained; }
    public void setTotalMarksObtained(Double totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }

    public Double getPercentageScore() { return percentageScore; }
    public void setPercentageScore(Double percentageScore) { this.percentageScore = percentageScore; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getIsPassed() { return isPassed; }
    public void setIsPassed(String isPassed) { this.isPassed = isPassed; }

    public String getProviderUsed() { return providerUsed; }
    public void setProviderUsed(String providerUsed) { this.providerUsed = providerUsed; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public String getRubricBreakdownJson() { return rubricBreakdownJson; }
    public void setRubricBreakdownJson(String rubricBreakdownJson) { this.rubricBreakdownJson = rubricBreakdownJson; }

    public String getStrengthsJson() { return strengthsJson; }
    public void setStrengthsJson(String strengthsJson) { this.strengthsJson = strengthsJson; }

    public String getImprovementsJson() { return improvementsJson; }
    public void setImprovementsJson(String improvementsJson) { this.improvementsJson = improvementsJson; }

    public String getDetailedFeedback() { return detailedFeedback; }
    public void setDetailedFeedback(String detailedFeedback) { this.detailedFeedback = detailedFeedback; }

    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }

    public Boolean getIsTeacherOverridden() { return isTeacherOverridden; }
    public void setIsTeacherOverridden(Boolean isTeacherOverridden) { this.isTeacherOverridden = isTeacherOverridden; }

    public String getTeacherNotes() { return teacherNotes; }
    public void setTeacherNotes(String teacherNotes) { this.teacherNotes = teacherNotes; }
}
