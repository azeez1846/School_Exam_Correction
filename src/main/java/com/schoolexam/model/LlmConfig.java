package com.schoolexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "llm_configs")
public class LlmConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String providerKey; // gemini, groq, huggingface, openrouter, local

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String modelName;

    private String apiKey;
    private Boolean isFreeTier;
    private Boolean isDefault;

    private LocalDateTime updatedAt;

    public LlmConfig() {}

    public LlmConfig(Long id, String providerKey, String displayName, String modelName, String apiKey, Boolean isFreeTier, Boolean isDefault, LocalDateTime updatedAt) {
        this.id = id;
        this.providerKey = providerKey;
        this.displayName = displayName;
        this.modelName = modelName;
        this.apiKey = apiKey;
        this.isFreeTier = isFreeTier;
        this.isDefault = isDefault;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
    }

    public static LlmConfigBuilder builder() {
        return new LlmConfigBuilder();
    }

    public static class LlmConfigBuilder {
        private Long id;
        private String providerKey;
        private String displayName;
        private String modelName;
        private String apiKey;
        private Boolean isFreeTier;
        private Boolean isDefault;
        private LocalDateTime updatedAt;

        public LlmConfigBuilder id(Long id) { this.id = id; return this; }
        public LlmConfigBuilder providerKey(String providerKey) { this.providerKey = providerKey; return this; }
        public LlmConfigBuilder displayName(String displayName) { this.displayName = displayName; return this; }
        public LlmConfigBuilder modelName(String modelName) { this.modelName = modelName; return this; }
        public LlmConfigBuilder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public LlmConfigBuilder isFreeTier(Boolean isFreeTier) { this.isFreeTier = isFreeTier; return this; }
        public LlmConfigBuilder isDefault(Boolean isDefault) { this.isDefault = isDefault; return this; }
        public LlmConfigBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public LlmConfig build() {
            return new LlmConfig(id, providerKey, displayName, modelName, apiKey, isFreeTier, isDefault, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProviderKey() { return providerKey; }
    public void setProviderKey(String providerKey) { this.providerKey = providerKey; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public Boolean getIsFreeTier() { return isFreeTier; }
    public void setIsFreeTier(Boolean isFreeTier) { this.isFreeTier = isFreeTier; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
