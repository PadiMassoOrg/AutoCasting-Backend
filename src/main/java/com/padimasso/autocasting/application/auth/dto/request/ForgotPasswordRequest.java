package com.padimasso.autocasting.application.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
    @NotBlank(message = "auth.required_field") @Email(message = "auth.email_invalid") String email
) {
}
