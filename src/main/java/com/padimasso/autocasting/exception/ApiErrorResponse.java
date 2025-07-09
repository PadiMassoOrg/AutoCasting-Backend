package com.padimasso.autocasting.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        Map<String, String> errors,
        String path
) {
}