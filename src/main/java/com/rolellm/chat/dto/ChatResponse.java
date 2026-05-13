package com.rolellm.chat.dto;

import java.util.Map;

public record ChatResponse(
        String reply,
        String model,
        Map<String, Object> usage
) {
}
