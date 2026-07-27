package com.schoolexam.controller;

import com.schoolexam.dto.EvaluationDtos.*;
import com.schoolexam.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }

    @GetMapping("/exam/{examId}/item-analysis")
    public ResponseEntity<ItemAnalysisDto> getItemAnalysis(@PathVariable Long examId) {
        return ResponseEntity.ok(analyticsService.getItemAnalysisForExam(examId));
    }

    @GetMapping("/exam/{examId}/misconceptions")
    public ResponseEntity<MisconceptionClusterDto> getMisconceptionClusters(@PathVariable Long examId) {
        return ResponseEntity.ok(analyticsService.getMisconceptionClustersForExam(examId));
    }
}
