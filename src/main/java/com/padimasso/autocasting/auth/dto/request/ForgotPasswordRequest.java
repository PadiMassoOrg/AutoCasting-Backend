package com.padimasso.autocasting.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
    @NotBlank @Email(message = "auth.email_invalid") String email
) {
}
