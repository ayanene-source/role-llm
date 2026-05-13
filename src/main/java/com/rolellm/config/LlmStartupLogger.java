package com.rolellm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LlmStartupLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LlmStartupLogger.class);

    private final LlmProperties properties;

    public LlmStartupLogger(LlmProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("LLM config loaded: baseUrl={}, model={}, temperature={}, apiKey={}",
                properties.baseUrl(),
                properties.model(),
                properties.resolvedTemperature(),
                maskApiKey(properties.apiKey()));
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return "NOT_SET";
        }
        if (apiKey.length() <= 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
