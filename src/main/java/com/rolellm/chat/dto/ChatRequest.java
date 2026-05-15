package com.rolellm.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class ChatRequest {

    @NotBlank(message = "message must not be blank")
    @Size(max = 8000, message = "message must not exceed 8000 characters")
    private String message;

    @Size(max = 128, message = "conversationId must not exceed 128 characters")
    private String conversationId;

    public ChatRequest() {
    }

    public ChatRequest(String message, String conversationId) {
        this.message = message;
        this.conversationId = conversationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
