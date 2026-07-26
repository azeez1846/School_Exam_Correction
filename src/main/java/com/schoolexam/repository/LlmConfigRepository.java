package com.schoolexam.repository;

import com.schoolexam.model.LlmConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LlmConfigRepository extends JpaRepository<LlmConfig, Long> {
    Optional<LlmConfig> findByProviderKey(String providerKey);
    Optional<LlmConfig> findByIsDefaultTrue();
}
