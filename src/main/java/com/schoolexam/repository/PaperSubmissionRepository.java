package com.schoolexam.repository;

import com.schoolexam.model.PaperSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaperSubmissionRepository extends JpaRepository<PaperSubmission, Long> {
    List<PaperSubmission> findByExamId(Long examId);
    List<PaperSubmission> findByStatus(String status);
    Long countByStatus(String status);
}
