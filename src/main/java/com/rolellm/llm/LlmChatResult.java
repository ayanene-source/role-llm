package com.rolellm.llm;

import java.util.Map;

public class LlmChatResult {

    private String reply;
    private String model;
    private Map<String, Object> usage;

    public LlmChatResult() {
    }

    public LlmChatResult(String reply, String model, Map<String, Object> usage) {
        this.reply = reply;
        this.model = model;
        this.usage = usage;
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
}
