package com.padimasso.autocasting.application.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank(message = "auth.required_field")
    String token,
    @NotBlank(message = "auth.required_field")
    @Size(min = 6, max = 255, message = "auth.password_length")
    String newPassword
) {
}
