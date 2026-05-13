package com.rolellm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "llm")
public record LlmProperties(
        String baseUrl,
        String apiKey,
        String model,
        Double temperature
) {
    public double resolvedTemperature() {
        return temperature == null ? 0.7d : temperature;
    }
}
