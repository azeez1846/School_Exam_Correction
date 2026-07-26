package com.schoolexam.service;

import com.schoolexam.dto.EvaluationDtos.EvaluationDetailDto;
import com.schoolexam.dto.EvaluationDtos.EvaluationRequest;
import com.schoolexam.model.PaperSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class BulkProcessingService {

    @Autowired
    private PaperSubmissionService submissionService;

    @Autowired
    private LlmEvaluationService evaluationService;

    public Map<String, Object> processBulkUpload(Long examId, List<MultipartFile> files, String providerKey, String modelName) {
        List<Map<String, Object>> processedResults = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        if (files == null || files.isEmpty()) {
            return Map.of("message", "No files provided for bulk processing", "processedCount", 0);
        }

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "Student_Paper_" + (i+1);
            
            // Extract student name and roll number from filename or generate default
            String studentName = deriveStudentName(filename, i + 1);
            String rollNumber = "S2026-" + String.format("%03d", i + 101);

            try {
                PaperSubmission submission = submissionService.submitPaper(examId, studentName, rollNumber, file, null);

                EvaluationRequest evalReq = new EvaluationRequest();
                evalReq.setSubmissionId(submission.getId());
                evalReq.setProviderKey(providerKey != null ? providerKey : "gemini");
                evalReq.setModelName(modelName != null ? modelName : "gemini-1.5-flash");

                EvaluationDetailDto evalResult = evaluationService.evaluatePaper(evalReq);

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("submissionId", submission.getId());
                itemMap.put("studentName", studentName);
                itemMap.put("rollNumber", rollNumber);
                itemMap.put("score", evalResult.getTotalMarksObtained());
                itemMap.put("percentage", evalResult.getPercentageScore());
                itemMap.put("grade", evalResult.getGrade());
                itemMap.put("status", evalResult.getIsPassed());
                itemMap.put("feedback", evalResult.getDetailedFeedback());
                
                processedResults.add(itemMap);
                successCount++;
            } catch (Exception e) {
                failCount++;
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("filename", filename);
                errorMap.put("error", e.getMessage());
                errorMap.put("status", "FAILED");
                processedResults.add(errorMap);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalFiles", files.size());
        response.put("successCount", successCount);
        response.put("failCount", failCount);
        response.put("results", processedResults);

        return response;
    }

    private String deriveStudentName(String filename, int index) {
        String baseName = filename.replaceAll("(?i)\\.(jpg|jpeg|png|pdf|txt)$", "");
        baseName = baseName.replaceAll("[_-]", " ").trim();
        if (baseName.length() > 2 && !baseName.toLowerCase().startsWith("paper")) {
            return capitalizeWords(baseName);
        }
        String[] sampleNames = {"Alex Rivera", "Sophia Chen", "Marcus Vance", "Emma Watson", "David Miller", "Liam Johnson", "Olivia Davis", "Noah Wilson"};
        return sampleNames[(index - 1) % sampleNames.length];
    }

    private String capitalizeWords(String input) {
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
