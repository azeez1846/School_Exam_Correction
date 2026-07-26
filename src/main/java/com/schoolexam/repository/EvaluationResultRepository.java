package com.schoolexam.repository;

import com.schoolexam.model.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {
    Optional<EvaluationResult> findBySubmissionId(Long submissionId);
}
