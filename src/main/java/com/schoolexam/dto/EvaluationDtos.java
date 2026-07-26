package com.schoolexam.dto;

import java.util.List;

public class EvaluationDtos {

    public static class EvaluationRequest {
        private Long submissionId;
        private String providerKey; // gemini, groq, huggingface, openrouter, local
        private String modelName;
        private String ocrTextOverride;
        private String customApiKey;

        public EvaluationRequest() {}

        public EvaluationRequest(Long submissionId, String providerKey, String modelName, String ocrTextOverride, String customApiKey) {
            this.submissionId = submissionId;
            this.providerKey = providerKey;
            this.modelName = modelName;
            this.ocrTextOverride = ocrTextOverride;
            this.customApiKey = customApiKey;
        }

        public Long getSubmissionId() { return submissionId; }
        public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

        public String getProviderKey() { return providerKey; }
        public void setProviderKey(String providerKey) { this.providerKey = providerKey; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public String getOcrTextOverride() { return ocrTextOverride; }
        public void setOcrTextOverride(String ocrTextOverride) { this.ocrTextOverride = ocrTextOverride; }

        public String getCustomApiKey() { return customApiKey; }
        public void setCustomApiKey(String customApiKey) { this.customApiKey = customApiKey; }
    }

    public static class EvaluationDetailDto {
        private Long id;
        private Long submissionId;
        private String studentName;
        private String rollNumber;
        private String examTitle;
        private String subject;
        private Double totalMarksObtained;
        private Double maxMarks;
        private Double percentageScore;
        private String grade;
        private String isPassed;
        private String providerUsed;
        private String modelUsed;
        private String ocrText;
        private List<RubricItemScore> rubricBreakdown;
        private List<String> keyStrengths;
        private List<String> improvementAreas;
        private String detailedFeedback;
        private String evaluatedAt;

        public EvaluationDetailDto() {}

        public EvaluationDetailDto(Long id, Long submissionId, String studentName, String rollNumber, String examTitle, String subject, Double totalMarksObtained, Double maxMarks, Double percentageScore, String grade, String isPassed, String providerUsed, String modelUsed, String ocrText, List<RubricItemScore> rubricBreakdown, List<String> keyStrengths, List<String> improvementAreas, String detailedFeedback, String evaluatedAt) {
            this.id = id;
            this.submissionId = submissionId;
            this.studentName = studentName;
            this.rollNumber = rollNumber;
            this.examTitle = examTitle;
            this.subject = subject;
            this.totalMarksObtained = totalMarksObtained;
            this.maxMarks = maxMarks;
            this.percentageScore = percentageScore;
            this.grade = grade;
            this.isPassed = isPassed;
            this.providerUsed = providerUsed;
            this.modelUsed = modelUsed;
            this.ocrText = ocrText;
            this.rubricBreakdown = rubricBreakdown;
            this.keyStrengths = keyStrengths;
            this.improvementAreas = improvementAreas;
            this.detailedFeedback = detailedFeedback;
            this.evaluatedAt = evaluatedAt;
        }

        public static EvaluationDetailDtoBuilder builder() {
            return new EvaluationDetailDtoBuilder();
        }

        public static class EvaluationDetailDtoBuilder {
            private Long id;
            private Long submissionId;
            private String studentName;
            private String rollNumber;
            private String examTitle;
            private String subject;
            private Double totalMarksObtained;
            private Double maxMarks;
            private Double percentageScore;
            private String grade;
            private String isPassed;
            private String providerUsed;
            private String modelUsed;
            private String ocrText;
            private List<RubricItemScore> rubricBreakdown;
            private List<String> keyStrengths;
            private List<String> improvementAreas;
            private String detailedFeedback;
            private String evaluatedAt;

            public EvaluationDetailDtoBuilder id(Long id) { this.id = id; return this; }
            public EvaluationDetailDtoBuilder submissionId(Long submissionId) { this.submissionId = submissionId; return this; }
            public EvaluationDetailDtoBuilder studentName(String studentName) { this.studentName = studentName; return this; }
            public EvaluationDetailDtoBuilder rollNumber(String rollNumber) { this.rollNumber = rollNumber; return this; }
            public EvaluationDetailDtoBuilder examTitle(String examTitle) { this.examTitle = examTitle; return this; }
            public EvaluationDetailDtoBuilder subject(String subject) { this.subject = subject; return this; }
            public EvaluationDetailDtoBuilder totalMarksObtained(Double totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; return this; }
            public EvaluationDetailDtoBuilder maxMarks(Double maxMarks) { this.maxMarks = maxMarks; return this; }
            public EvaluationDetailDtoBuilder percentageScore(Double percentageScore) { this.percentageScore = percentageScore; return this; }
            public EvaluationDetailDtoBuilder grade(String grade) { this.grade = grade; return this; }
            public EvaluationDetailDtoBuilder isPassed(String isPassed) { this.isPassed = isPassed; return this; }
            public EvaluationDetailDtoBuilder providerUsed(String providerUsed) { this.providerUsed = providerUsed; return this; }
            public EvaluationDetailDtoBuilder modelUsed(String modelUsed) { this.modelUsed = modelUsed; return this; }
            public EvaluationDetailDtoBuilder ocrText(String ocrText) { this.ocrText = ocrText; return this; }
            public EvaluationDetailDtoBuilder rubricBreakdown(List<RubricItemScore> rubricBreakdown) { this.rubricBreakdown = rubricBreakdown; return this; }
            public EvaluationDetailDtoBuilder keyStrengths(List<String> keyStrengths) { this.keyStrengths = keyStrengths; return this; }
            public EvaluationDetailDtoBuilder improvementAreas(List<String> improvementAreas) { this.improvementAreas = improvementAreas; return this; }
            public EvaluationDetailDtoBuilder detailedFeedback(String detailedFeedback) { this.detailedFeedback = detailedFeedback; return this; }
            public EvaluationDetailDtoBuilder evaluatedAt(String evaluatedAt) { this.evaluatedAt = evaluatedAt; return this; }

            public EvaluationDetailDto build() {
                return new EvaluationDetailDto(id, submissionId, studentName, rollNumber, examTitle, subject, totalMarksObtained, maxMarks, percentageScore, grade, isPassed, providerUsed, modelUsed, ocrText, rubricBreakdown, keyStrengths, improvementAreas, detailedFeedback, evaluatedAt);
            }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getSubmissionId() { return submissionId; }
        public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public String getRollNumber() { return rollNumber; }
        public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

        public String getExamTitle() { return examTitle; }
        public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

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

        public String getOcrText() { return ocrText; }
        public void setOcrText(String ocrText) { this.ocrText = ocrText; }

        public List<RubricItemScore> getRubricBreakdown() { return rubricBreakdown; }
        public void setRubricBreakdown(List<RubricItemScore> rubricBreakdown) { this.rubricBreakdown = rubricBreakdown; }

        public List<String> getKeyStrengths() { return keyStrengths; }
        public void setKeyStrengths(List<String> keyStrengths) { this.keyStrengths = keyStrengths; }

        public List<String> getImprovementAreas() { return improvementAreas; }
        public void setImprovementAreas(List<String> improvementAreas) { this.improvementAreas = improvementAreas; }

        public String getDetailedFeedback() { return detailedFeedback; }
        public void setDetailedFeedback(String detailedFeedback) { this.detailedFeedback = detailedFeedback; }

        public String getEvaluatedAt() { return evaluatedAt; }
        public void setEvaluatedAt(String evaluatedAt) { this.evaluatedAt = evaluatedAt; }
    }

    public static class RubricItemScore {
        private String criteriaName;
        private Double scoreObtained;
        private Double maxScore;
        private String feedback;

        public RubricItemScore() {}

        public RubricItemScore(String criteriaName, Double scoreObtained, Double maxScore, String feedback) {
            this.criteriaName = criteriaName;
            this.scoreObtained = scoreObtained;
            this.maxScore = maxScore;
            this.feedback = feedback;
        }

        public String getCriteriaName() { return criteriaName; }
        public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }

        public Double getScoreObtained() { return scoreObtained; }
        public void setScoreObtained(Double scoreObtained) { this.scoreObtained = scoreObtained; }

        public Double getMaxScore() { return maxScore; }
        public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }

        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
    }

    public static class DashboardStats {
        private long totalExams;
        private long totalSubmissions;
        private long completedEvaluations;
        private double classAveragePercentage;
        private double passPercentageRate;
        private List<ScoreRangeDistribution> distribution;

        public DashboardStats() {}

        public DashboardStats(long totalExams, long totalSubmissions, long completedEvaluations, double classAveragePercentage, double passPercentageRate, List<ScoreRangeDistribution> distribution) {
            this.totalExams = totalExams;
            this.totalSubmissions = totalSubmissions;
            this.completedEvaluations = completedEvaluations;
            this.classAveragePercentage = classAveragePercentage;
            this.passPercentageRate = passPercentageRate;
            this.distribution = distribution;
        }

        public static DashboardStatsBuilder builder() {
            return new DashboardStatsBuilder();
        }

        public static class DashboardStatsBuilder {
            private long totalExams;
            private long totalSubmissions;
            private long completedEvaluations;
            private double classAveragePercentage;
            private double passPercentageRate;
            private List<ScoreRangeDistribution> distribution;

            public DashboardStatsBuilder totalExams(long totalExams) { this.totalExams = totalExams; return this; }
            public DashboardStatsBuilder totalSubmissions(long totalSubmissions) { this.totalSubmissions = totalSubmissions; return this; }
            public DashboardStatsBuilder completedEvaluations(long completedEvaluations) { this.completedEvaluations = completedEvaluations; return this; }
            public DashboardStatsBuilder classAveragePercentage(double classAveragePercentage) { this.classAveragePercentage = classAveragePercentage; return this; }
            public DashboardStatsBuilder passPercentageRate(double passPercentageRate) { this.passPercentageRate = passPercentageRate; return this; }
            public DashboardStatsBuilder distribution(List<ScoreRangeDistribution> distribution) { this.distribution = distribution; return this; }

            public DashboardStats build() {
                return new DashboardStats(totalExams, totalSubmissions, completedEvaluations, classAveragePercentage, passPercentageRate, distribution);
            }
        }

        public long getTotalExams() { return totalExams; }
        public void setTotalExams(long totalExams) { this.totalExams = totalExams; }

        public long getTotalSubmissions() { return totalSubmissions; }
        public void setTotalSubmissions(long totalSubmissions) { this.totalSubmissions = totalSubmissions; }

        public long getCompletedEvaluations() { return completedEvaluations; }
        public void setCompletedEvaluations(long completedEvaluations) { this.completedEvaluations = completedEvaluations; }

        public double getClassAveragePercentage() { return classAveragePercentage; }
        public void setClassAveragePercentage(double classAveragePercentage) { this.classAveragePercentage = classAveragePercentage; }

        public double getPassPercentageRate() { return passPercentageRate; }
        public void setPassPercentageRate(double passPercentageRate) { this.passPercentageRate = passPercentageRate; }

        public List<ScoreRangeDistribution> getDistribution() { return distribution; }
        public void setDistribution(List<ScoreRangeDistribution> distribution) { this.distribution = distribution; }
    }

    public static class ScoreRangeDistribution {
        private String rangeLabel;
        private int count;

        public ScoreRangeDistribution() {}

        public ScoreRangeDistribution(String rangeLabel, int count) {
            this.rangeLabel = rangeLabel;
            this.count = count;
        }

        public String getRangeLabel() { return rangeLabel; }
        public void setRangeLabel(String rangeLabel) { this.rangeLabel = rangeLabel; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}
