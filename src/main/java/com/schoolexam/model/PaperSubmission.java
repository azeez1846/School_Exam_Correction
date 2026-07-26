package com.schoolexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "paper_submissions")
public class PaperSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long examId;

    private String studentName;
    private String rollNumber;
    private String fileName;
    private String fileType;

    @Column(length = 10000)
    private String ocrText;

    private String status; // PENDING, PROCESSING, COMPLETED, FAILED

    private LocalDateTime createdAt;

    public PaperSubmission() {}

    public PaperSubmission(Long id, Long examId, String studentName, String rollNumber, String fileName, String fileType, String ocrText, String status, LocalDateTime createdAt) {
        this.id = id;
        this.examId = examId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.fileName = fileName;
        this.fileType = fileType;
        this.ocrText = ocrText;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    public static PaperSubmissionBuilder builder() {
        return new PaperSubmissionBuilder();
    }

    public static class PaperSubmissionBuilder {
        private Long id;
        private Long examId;
        private String studentName;
        private String rollNumber;
        private String fileName;
        private String fileType;
        private String ocrText;
        private String status;
        private LocalDateTime createdAt;

        public PaperSubmissionBuilder id(Long id) { this.id = id; return this; }
        public PaperSubmissionBuilder examId(Long examId) { this.examId = examId; return this; }
        public PaperSubmissionBuilder studentName(String studentName) { this.studentName = studentName; return this; }
        public PaperSubmissionBuilder rollNumber(String rollNumber) { this.rollNumber = rollNumber; return this; }
        public PaperSubmissionBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public PaperSubmissionBuilder fileType(String fileType) { this.fileType = fileType; return this; }
        public PaperSubmissionBuilder ocrText(String ocrText) { this.ocrText = ocrText; return this; }
        public PaperSubmissionBuilder status(String status) { this.status = status; return this; }
        public PaperSubmissionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PaperSubmission build() {
            return new PaperSubmission(id, examId, studentName, rollNumber, fileName, fileType, ocrText, status, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getOcrText() { return ocrText; }
    public void setOcrText(String ocrText) { this.ocrText = ocrText; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
