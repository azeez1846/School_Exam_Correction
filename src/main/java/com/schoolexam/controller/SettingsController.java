package com.schoolexam.controller;

import com.schoolexam.model.LlmConfig;
import com.schoolexam.repository.LlmConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsController {

    @Autowired
    private LlmConfigRepository llmConfigRepository;

    @GetMapping("/llm")
    public ResponseEntity<List<LlmConfig>> getLlmConfigs() {
        return ResponseEntity.ok(llmConfigRepository.findAll());
    }

    @PostMapping("/llm")
    public ResponseEntity<LlmConfig> saveOrUpdateLlmConfig(@RequestBody LlmConfig config) {
        Optional<LlmConfig> existing = llmConfigRepository.findByProviderKey(config.getProviderKey());
        if (existing.isPresent()) {
            LlmConfig entity = existing.get();
            entity.setDisplayName(config.getDisplayName());
            entity.setModelName(config.getModelName());
            entity.setApiKey(config.getApiKey());
            entity.setIsFreeTier(config.getIsFreeTier());
            entity.setIsDefault(config.getIsDefault());
            return ResponseEntity.ok(llmConfigRepository.save(entity));
        } else {
            return ResponseEntity.ok(llmConfigRepository.save(config));
        }
    }
}
