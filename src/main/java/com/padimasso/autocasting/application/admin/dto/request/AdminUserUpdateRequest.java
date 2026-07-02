package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.auth.model.UserMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminUserUpdateRequest(
    @NotBlank(message = "validation.required")
    @Email(message = "auth.email_invalid")
    String email,
    UserMode activeMode,
    @NotBlank(message = "validation.required")
    String note
) {
}
