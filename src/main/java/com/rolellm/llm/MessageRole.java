package com.rolellm.llm;

public enum MessageRole {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String value;

    MessageRole(String value) {

        this.value = value;
    }

    public String value() {

        return value;
    }
}
