package com.schoolexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    private Double totalMarks;
    private Double passPercentage;

    @Column(length = 2000)
    private String instructions;

    @Column(length = 4000)
    private String answerKey;

    private LocalDateTime createdAt;

    public Exam() {}

    public Exam(Long id, String title, String subject, Double totalMarks, Double passPercentage, String instructions, String answerKey, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.totalMarks = totalMarks;
        this.passPercentage = passPercentage;
        this.instructions = instructions;
        this.answerKey = answerKey;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (totalMarks == null) {
            totalMarks = 100.0;
        }
        if (passPercentage == null) {
            passPercentage = 40.0;
        }
    }

    public static ExamBuilder builder() {
        return new ExamBuilder();
    }

    public static class ExamBuilder {
        private Long id;
        private String title;
        private String subject;
        private Double totalMarks;
        private Double passPercentage;
        private String instructions;
        private String answerKey;
        private LocalDateTime createdAt;

        public ExamBuilder id(Long id) { this.id = id; return this; }
        public ExamBuilder title(String title) { this.title = title; return this; }
        public ExamBuilder subject(String subject) { this.subject = subject; return this; }
        public ExamBuilder totalMarks(Double totalMarks) { this.totalMarks = totalMarks; return this; }
        public ExamBuilder passPercentage(Double passPercentage) { this.passPercentage = passPercentage; return this; }
        public ExamBuilder instructions(String instructions) { this.instructions = instructions; return this; }
        public ExamBuilder answerKey(String answerKey) { this.answerKey = answerKey; return this; }
        public ExamBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Exam build() {
            return new Exam(id, title, subject, totalMarks, passPercentage, instructions, answerKey, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Double totalMarks) { this.totalMarks = totalMarks; }

    public Double getPassPercentage() { return passPercentage; }
    public void setPassPercentage(Double passPercentage) { this.passPercentage = passPercentage; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getAnswerKey() { return answerKey; }
    public void setAnswerKey(String answerKey) { this.answerKey = answerKey; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
