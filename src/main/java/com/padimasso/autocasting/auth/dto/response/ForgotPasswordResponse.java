package com.padimasso.autocasting.auth.dto.response;

import lombok.Builder;

@Builder
public record ForgotPasswordResponse(
    boolean success,
    String message
) {
}
