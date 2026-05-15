package com.rolellm.chat.dto;

import java.util.Map;

public class ChatResponse {

    private String reply;
    private String model;
    private Map<String, Object> usage;
    private String conversationId;

    public ChatResponse() {
    }

    public ChatResponse(String reply, String model, Map<String, Object> usage, String conversationId) {
        this.reply = reply;
        this.model = model;
        this.usage = usage;
        this.conversationId = conversationId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Map<String, Object> getUsage() {
        return usage;
    }

    public void setUsage(Map<String, Object> usage) {
        this.usage = usage;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
