package com.rolellm.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "message must not be blank")
        @Size(max = 8000, message = "message must not exceed 8000 characters")
        String message
) {
}
