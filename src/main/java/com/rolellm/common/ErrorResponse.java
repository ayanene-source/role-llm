package com.rolellm.common;

public record ErrorResponse(
        String error,
        String message
) {
}
