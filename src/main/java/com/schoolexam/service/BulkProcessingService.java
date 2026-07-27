package com.schoolexam.service;

import com.schoolexam.dto.EvaluationDtos.EvaluationDetailDto;
import com.schoolexam.dto.EvaluationDtos.EvaluationRequest;
import com.schoolexam.model.PaperSubmission;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Map<String, Object> processBulkPdf(Long examId, MultipartFile pdfFile, String providerKey, String modelName) {
        List<Map<String, Object>> processedResults = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        if (pdfFile == null || pdfFile.isEmpty()) {
            return Map.of("message", "No PDF file provided for batch processing", "processedCount", 0);
        }

        try {
            byte[] bytes = pdfFile.getBytes();
            PDDocument document = PDDocument.load(bytes);
            PDFTextStripper stripper = new PDFTextStripper();
            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Pattern rollPattern = Pattern.compile("(?i)(?:roll|student\\s*id|id)\\s*[:#-]?\\s*([A-Z0-9-]+)");
            Pattern namePattern = Pattern.compile("(?i)(?:name|student)\\s*[:#-]?\\s*([A-Za-z\\s]{3,25})");

            for (int i = 0; i < pageCount; i++) {
                int pageNum = i + 1;
                stripper.setStartPage(pageNum);
                stripper.setEndPage(pageNum);
                String pageText = stripper.getText(document);

                String rollNumber = null;
                Matcher rollMatcher = rollPattern.matcher(pageText);
                if (rollMatcher.find()) {
                    rollNumber = rollMatcher.group(1).trim();
                } else {
                    rollNumber = "PDF-S" + String.format("%03d", pageNum + 100);
                }

                String studentName = null;
                Matcher nameMatcher = namePattern.matcher(pageText);
                if (nameMatcher.find()) {
                    studentName = capitalizeWords(nameMatcher.group(1).trim());
                } else {
                    studentName = deriveStudentName(pdfFile.getOriginalFilename(), pageNum);
                }

                BufferedImage bim = renderer.renderImageWithDPI(i, 150);
                String imgFileName = "pdf_split_p" + pageNum + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
                File saveFile = uploadDir.resolve(imgFileName).toFile();
                ImageIO.write(bim, "PNG", saveFile);

                try {
                    PaperSubmission submission = submissionService.submitPaper(examId, studentName, rollNumber, null, pageText);
                    submission.setFileName(imgFileName);

                    EvaluationRequest evalReq = new EvaluationRequest();
                    evalReq.setSubmissionId(submission.getId());
                    evalReq.setProviderKey(providerKey != null ? providerKey : "gemini");
                    evalReq.setModelName(modelName != null ? modelName : "gemini-1.5-flash");

                    EvaluationDetailDto evalResult = evaluationService.evaluatePaper(evalReq);

                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("page", pageNum);
                    itemMap.put("submissionId", submission.getId());
                    itemMap.put("studentName", studentName);
                    itemMap.put("rollNumber", rollNumber);
                    itemMap.put("score", evalResult.getTotalMarksObtained());
                    itemMap.put("percentage", evalResult.getPercentageScore());
                    itemMap.put("grade", evalResult.getGrade());
                    itemMap.put("status", evalResult.getIsPassed());

                    processedResults.add(itemMap);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    Map<String, Object> errMap = new HashMap<>();
                    errMap.put("page", pageNum);
                    errMap.put("error", e.getMessage());
                    errMap.put("status", "FAILED");
                    processedResults.add(errMap);
                }
            }

            document.close();

            Map<String, Object> response = new HashMap<>();
            response.put("totalPages", pageCount);
            response.put("successCount", successCount);
            response.put("failCount", failCount);
            response.put("results", processedResults);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to process multi-page PDF document: " + e.getMessage(), e);
        }
    }

    private String deriveStudentName(String filename, int index) {
        String baseName = filename != null ? filename.replaceAll("(?i)\\.(jpg|jpeg|png|pdf|txt)$", "") : "Paper_" + index;
        baseName = baseName.replaceAll("[_-]", " ").trim();
        if (baseName.length() > 2 && !baseName.toLowerCase().startsWith("paper")) {
            return capitalizeWords(baseName);
        }
        String[] sampleNames = {"Alex Rivera", "Sophia Chen", "Marcus Vance", "Emma Watson", "David Miller", "Liam Johnson", "Olivia Davis", "Noah Wilson"};
        return sampleNames[(index - 1) % sampleNames.length];
    }

    private String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) return "";
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
