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
}
