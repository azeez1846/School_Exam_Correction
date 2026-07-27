package com.schoolexam.service;

import com.schoolexam.dto.EvaluationDtos.*;
import com.schoolexam.model.EvaluationResult;
import com.schoolexam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private PaperSubmissionRepository submissionRepository;

    @Autowired
    private EvaluationResultRepository resultRepository;

    public DashboardStats getDashboardStats() {
        long totalExams = examRepository.count();
        long totalSubmissions = submissionRepository.count();
        List<EvaluationResult> results = resultRepository.findAll();
        long completed = results.size();

        double totalPctSum = 0.0;
        long passedCount = 0;

        int countA = 0, countB = 0, countC = 0, countD = 0, countF = 0;

        for (EvaluationResult r : results) {
            double pct = r.getPercentageScore() != null ? r.getPercentageScore() : 0.0;
            totalPctSum += pct;
            if ("PASSED".equalsIgnoreCase(r.getIsPassed())) {
                passedCount++;
            }

            if (pct >= 90) countA++;
            else if (pct >= 80) countB++;
            else if (pct >= 70) countC++;
            else if (pct >= 60) countD++;
            else countF++;
        }

        double classAvg = completed > 0 ? Math.round((totalPctSum / completed) * 10.0) / 10.0 : 0.0;
        double passRate = completed > 0 ? Math.round(((double) passedCount / completed) * 1000.0) / 10.0 : 0.0;

        List<ScoreRangeDistribution> distribution = List.of(
                new ScoreRangeDistribution("A (90-100%)", countA),
                new ScoreRangeDistribution("B (80-89%)", countB),
                new ScoreRangeDistribution("C (70-79%)", countC),
                new ScoreRangeDistribution("D (60-69%)", countD),
                new ScoreRangeDistribution("F (<60%)", countF)
        );

        return DashboardStats.builder()
                .totalExams(totalExams)
                .totalSubmissions(totalSubmissions)
                .completedEvaluations(completed)
                .classAveragePercentage(classAvg)
                .passPercentageRate(passRate)
                .distribution(distribution)
                .build();
    }

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public ItemAnalysisDto getItemAnalysisForExam(Long examId) {
        com.schoolexam.model.Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

        List<com.schoolexam.model.PaperSubmission> submissions = submissionRepository.findByExamId(examId);
        List<EvaluationResult> results = new ArrayList<>();
        for (com.schoolexam.model.PaperSubmission sub : submissions) {
            resultRepository.findBySubmissionId(sub.getId()).ifPresent(results::add);
        }

        Map<String, double[]> criterionTotals = new LinkedHashMap<>();

        double totalPctSum = 0.0;
        for (EvaluationResult r : results) {
            if (r.getPercentageScore() != null) totalPctSum += r.getPercentageScore();
            if (r.getRubricBreakdownJson() != null) {
                try {
                    List<RubricItemScore> breakdown = objectMapper.readValue(r.getRubricBreakdownJson(), new com.fasterxml.jackson.core.type.TypeReference<List<RubricItemScore>>() {});
                    for (RubricItemScore item : breakdown) {
                        String name = item.getCriteriaName();
                        double score = item.getScoreObtained() != null ? item.getScoreObtained() : 0.0;
                        double max = item.getMaxScore() != null ? item.getMaxScore() : 10.0;
                        double[] curr = criterionTotals.computeIfAbsent(name, k -> new double[]{0.0, max, 0});
                        curr[0] += score;
                        curr[2] += 1;
                    }
                } catch (Exception ignored) {}
            }
        }

        double classAvgPct = results.size() > 0 ? Math.round((totalPctSum / results.size()) * 10.0) / 10.0 : 0.0;

        List<RubricStat> rubricStats = new ArrayList<>();
        for (Map.Entry<String, double[]> entry : criterionTotals.entrySet()) {
            String name = entry.getKey();
            double[] vals = entry.getValue();
            double count = vals[2];
            double maxScore = vals[1];
            double avgObtained = count > 0 ? Math.round((vals[0] / count) * 10.0) / 10.0 : 0.0;
            double pctClass = maxScore > 0 ? Math.round((avgObtained / maxScore) * 1000.0) / 10.0 : 0.0;

            String difficulty = "MODERATE";
            if (pctClass >= 80.0) difficulty = "EASY";
            else if (pctClass < 60.0) difficulty = "HARD";

            rubricStats.add(new RubricStat(name, maxScore, avgObtained, pctClass, difficulty));
        }

        return new ItemAnalysisDto(examId, exam.getTitle(), results.size(), classAvgPct, rubricStats);
    }

    public MisconceptionClusterDto getMisconceptionClustersForExam(Long examId) {
        com.schoolexam.model.Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

        List<com.schoolexam.model.PaperSubmission> submissions = submissionRepository.findByExamId(examId);
        List<EvaluationResult> results = new ArrayList<>();
        for (com.schoolexam.model.PaperSubmission sub : submissions) {
            resultRepository.findBySubmissionId(sub.getId()).ifPresent(results::add);
        }

        Map<String, Integer> errorCounts = new LinkedHashMap<>();
        for (EvaluationResult r : results) {
            if (r.getImprovementsJson() != null) {
                try {
                    List<String> items = objectMapper.readValue(r.getImprovementsJson(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                    for (String text : items) {
                        errorCounts.put(text, errorCounts.getOrDefault(text, 0) + 1);
                    }
                } catch (Exception ignored) {}
            }
        }

        int totalEvaluated = Math.max(results.size(), 1);
        List<MisconceptionItem> clusters = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : errorCounts.entrySet()) {
            String issue = entry.getKey();
            int count = entry.getValue();
            double pct = Math.round(((double) count / totalEvaluated) * 1000.0) / 10.0;

            String recommendation = "Review foundational concepts and assign targeted practice questions on " + issue.toLowerCase();
            clusters.add(new MisconceptionItem(issue, count, pct, recommendation));
        }

        clusters.sort((a, b) -> Integer.compare(b.getFrequencyCount(), a.getFrequencyCount()));

        return new MisconceptionClusterDto(examId, exam.getTitle(), clusters);
    }
}
