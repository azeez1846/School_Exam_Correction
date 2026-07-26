package com.schoolexam.repository;

import com.schoolexam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findBySubjectContainingIgnoreCase(String subject);
}
