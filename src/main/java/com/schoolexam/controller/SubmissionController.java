package com.schoolexam.controller;

import com.schoolexam.model.PaperSubmission;
import com.schoolexam.service.BulkProcessingService;
import com.schoolexam.service.PaperSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin(origins = "*")
public class SubmissionController {

    @Autowired
    private PaperSubmissionService submissionService;

    @Autowired
    private BulkProcessingService bulkProcessingService;

    @GetMapping
    public ResponseEntity<List<PaperSubmission>> getAllSubmissions() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaperSubmission> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.getSubmissionById(id));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<PaperSubmission>> getSubmissionsByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByExam(examId));
    }

    @PostMapping("/upload")
    public ResponseEntity<PaperSubmission> uploadSinglePaper(
            @RequestParam("examId") Long examId,
            @RequestParam(value = "studentName", required = false) String studentName,
            @RequestParam(value = "rollNumber", required = false) String rollNumber,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "manualTextOverride", required = false) String manualTextOverride
    ) throws IOException {
        PaperSubmission submission = submissionService.submitPaper(examId, studentName, rollNumber, file, manualTextOverride);
        return ResponseEntity.ok(submission);
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<Map<String, Object>> uploadBulkPapers(
            @RequestParam("examId") Long examId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "providerKey", required = false) String providerKey,
            @RequestParam(value = "modelName", required = false) String modelName
    ) {
        Map<String, Object> result = bulkProcessingService.processBulkUpload(examId, files, providerKey, modelName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bulk-pdf")
    public ResponseEntity<Map<String, Object>> uploadBulkPdf(
            @RequestParam("examId") Long examId,
            @RequestParam("file") MultipartFile pdfFile,
            @RequestParam(value = "providerKey", required = false) String providerKey,
            @RequestParam(value = "modelName", required = false) String modelName
    ) {
        Map<String, Object> result = bulkProcessingService.processBulkPdf(examId, pdfFile, providerKey, modelName);
        return ResponseEntity.ok(result);
    }
}
