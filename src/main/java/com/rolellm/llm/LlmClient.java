package com.rolellm.llm;

public interface LlmClient {

    LlmChatResult chat(LlmChatRequest request);
}
