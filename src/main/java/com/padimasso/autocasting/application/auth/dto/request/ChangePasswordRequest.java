package com.padimasso.autocasting.application.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "auth.required_field")
    @Size(min = 8, message = "auth.password_length")
    String oldPassword,
    @NotBlank(message = "auth.required_field")
    @Size(min = 8, message = "auth.password_length")
    String newPassword
) {
}
