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
}
