package com.padimasso.autocasting.application.auth.dto.response;

import lombok.Builder;

@Builder
public record ForgotPasswordResponse(
    boolean success,
    String message
) {
}
