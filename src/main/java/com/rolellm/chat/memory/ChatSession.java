package com.rolellm.chat.memory;

import com.rolellm.llm.PromptMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatSession {

    private String conversationId;
    private List<PromptMessage> messages = new ArrayList<>();
    private Instant lastAccessAt;

    public ChatSession() {
    }

    public ChatSession(String conversationId) {
        this.conversationId = conversationId;
        this.lastAccessAt = Instant.now();
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public List<PromptMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<PromptMessage> messages) {
        this.messages = messages;
    }

    public Instant getLastAccessAt() {
        return lastAccessAt;
    }

    public void setLastAccessAt(Instant lastAccessAt) {
        this.lastAccessAt = lastAccessAt;
    }
}
