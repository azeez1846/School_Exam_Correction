package com.schoolexam.controller;

import com.schoolexam.dto.EvaluationDtos.*;
import com.schoolexam.service.LlmEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "*")
public class EvaluationController {

    @Autowired
    private LlmEvaluationService evaluationService;

    @PostMapping("/process")
    public ResponseEntity<EvaluationDetailDto> processEvaluation(@RequestBody EvaluationRequest request) {
        EvaluationDetailDto dto = evaluationService.evaluatePaper(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<EvaluationDetailDto> getEvaluationBySubmissionId(@PathVariable Long submissionId) {
        return ResponseEntity.ok(evaluationService.getEvaluationBySubmissionId(submissionId));
    }

    @PostMapping("/{submissionId}/override")
    public ResponseEntity<EvaluationDetailDto> overrideEvaluation(@PathVariable Long submissionId, @RequestBody OverrideEvaluationRequest request) {
        request.setSubmissionId(submissionId);
        return ResponseEntity.ok(evaluationService.overrideEvaluation(request));
    }

    @GetMapping("/export/csv/{examId}")
    public ResponseEntity<String> exportCsvGradebook(@PathVariable Long examId) {
        String csv = evaluationService.exportCsvGradebook(examId);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=\"gradebook_exam_" + examId + ".csv\"")
                .body(csv);
    }

    @GetMapping(value = "/export/report-card/{submissionId}", produces = "text/html")
    public ResponseEntity<String> downloadReportCard(@PathVariable Long submissionId) {
        String html = evaluationService.generateReportCardHtml(submissionId);
        return ResponseEntity.ok()
                .header("Content-Type", "text/html;charset=UTF-8")
                .body(html);
    }
}
