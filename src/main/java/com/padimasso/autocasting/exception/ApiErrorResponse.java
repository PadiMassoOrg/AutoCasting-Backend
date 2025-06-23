package com.padimasso.autocasting.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        String path
) {}