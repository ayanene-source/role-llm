package com.rolellm;

import com.rolellm.config.LlmProperties;
import com.rolellm.tts.TtsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({LlmProperties.class, TtsProperties.class})
public class RoleLlmApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoleLlmApplication.class, args);
    }
}
