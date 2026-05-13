package com.rolellm.llm;

public record PromptMessage(
        String role,
        String content
) {
}
