package com.schoolexam.service;

import com.schoolexam.model.PaperSubmission;
import com.schoolexam.repository.PaperSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class PaperSubmissionService {

    @Autowired
    private PaperSubmissionRepository submissionRepository;

    @Autowired
    private OcrService ocrService;

    private static final String UPLOAD_DIR = "uploads/";

    public PaperSubmission submitPaper(Long examId, String studentName, String rollNumber,
                                        MultipartFile file, String manualTextOverride) throws IOException {
        
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String originalFilename = file != null && !file.isEmpty() ? file.getOriginalFilename() : "paper.png";
        String storedFileName = UUID.randomUUID().toString() + "_" + (originalFilename != null ? originalFilename : "paper.png");
        Path destinationPath = Paths.get(UPLOAD_DIR + storedFileName);

        if (file != null && !file.isEmpty()) {
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }

        String ocrText = ocrService.extractTextFromPaper(file, manualTextOverride);

        PaperSubmission submission = PaperSubmission.builder()
                .examId(examId)
                .studentName(studentName != null && !studentName.isEmpty() ? studentName : "Student " + (int)(Math.random()*900 + 100))
                .rollNumber(rollNumber != null && !rollNumber.isEmpty() ? rollNumber : "ROLL-" + (int)(Math.random()*9000 + 1000))
                .fileName(storedFileName)
                .fileType(file != null ? file.getContentType() : "image/jpeg")
                .ocrText(ocrText)
                .status("PENDING")
                .build();

        return submissionRepository.save(submission);
    }

    public List<PaperSubmission> getSubmissionsByExam(Long examId) {
        return submissionRepository.findByExamId(examId);
    }

    public List<PaperSubmission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public PaperSubmission getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with ID: " + id));
    }
}
