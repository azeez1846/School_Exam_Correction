package com.schoolexam.controller;

import com.schoolexam.model.Exam;
import com.schoolexam.model.Rubric;
import com.schoolexam.repository.ExamRepository;
import com.schoolexam.repository.RubricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private RubricRepository rubricRepository;

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        return examRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        Exam saved = examRepository.save(exam);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}/rubrics")
    public ResponseEntity<List<Rubric>> getRubricsForExam(@PathVariable Long id) {
        return ResponseEntity.ok(rubricRepository.findByExamId(id));
    }

    @PostMapping("/{id}/rubrics")
    public ResponseEntity<Rubric> addRubricToExam(@PathVariable Long id, @RequestBody Rubric rubric) {
        rubric.setExamId(id);
        Rubric saved = rubricRepository.save(rubric);
        return ResponseEntity.ok(saved);
    }
}
