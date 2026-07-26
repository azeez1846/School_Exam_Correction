package com.schoolexam.repository;

import com.schoolexam.model.Rubric;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RubricRepository extends JpaRepository<Rubric, Long> {
    List<Rubric> findByExamId(Long examId);
}
