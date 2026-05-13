package com.rolellm.llm;

import java.util.Map;

public record LlmChatResult(
        String reply,
        String model,
        Map<String, Object> usage
) {
}
