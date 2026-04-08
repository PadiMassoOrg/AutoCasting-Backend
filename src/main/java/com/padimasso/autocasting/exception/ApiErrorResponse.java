package com.padimasso.autocasting.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record ApiErrorResponse(
    LocalDateTime timestamp,
    int status,
    String message,
    List<String> messageArgs,
    Map<String, ApiErrorMessage> errors,
    String path
) {
}
