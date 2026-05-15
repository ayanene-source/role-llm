package com.rolellm.llm;

import java.util.List;

public class LlmChatRequest {

    private List<PromptMessage> messages;

    public LlmChatRequest() {
    }

    public LlmChatRequest(List<PromptMessage> messages) {
        this.messages = messages;
    }

    public List<PromptMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<PromptMessage> messages) {
        this.messages = messages;
    }
}
