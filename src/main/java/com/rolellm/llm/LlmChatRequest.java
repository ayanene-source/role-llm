package com.rolellm.llm;

import java.util.List;

public record LlmChatRequest(
        List<PromptMessage> messages
) {
}
