package com.padimasso.autocasting.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank String token,
    @NotBlank @Size(min = 8, message = "auth.password_length") String newPassword
) {
}
