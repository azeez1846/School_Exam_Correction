package com.schoolexam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rubrics")
public class Rubric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long examId;

    @Column(nullable = false)
    private String criteriaName;

    private Double maxScore;
    private Double weightPercentage;

    @Column(length = 1000)
    private String description;

    public Rubric() {}

    public Rubric(Long id, Long examId, String criteriaName, Double maxScore, Double weightPercentage, String description) {
        this.id = id;
        this.examId = examId;
        this.criteriaName = criteriaName;
        this.maxScore = maxScore;
        this.weightPercentage = weightPercentage;
        this.description = description;
    }

    public static RubricBuilder builder() {
        return new RubricBuilder();
    }

    public static class RubricBuilder {
        private Long id;
        private Long examId;
        private String criteriaName;
        private Double maxScore;
        private Double weightPercentage;
        private String description;

        public RubricBuilder id(Long id) { this.id = id; return this; }
        public RubricBuilder examId(Long examId) { this.examId = examId; return this; }
        public RubricBuilder criteriaName(String criteriaName) { this.criteriaName = criteriaName; return this; }
        public RubricBuilder maxScore(Double maxScore) { this.maxScore = maxScore; return this; }
        public RubricBuilder weightPercentage(Double weightPercentage) { this.weightPercentage = weightPercentage; return this; }
        public RubricBuilder description(String description) { this.description = description; return this; }

        public Rubric build() {
            return new Rubric(id, examId, criteriaName, maxScore, weightPercentage, description);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getCriteriaName() { return criteriaName; }
    public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }

    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }

    public Double getWeightPercentage() { return weightPercentage; }
    public void setWeightPercentage(Double weightPercentage) { this.weightPercentage = weightPercentage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
