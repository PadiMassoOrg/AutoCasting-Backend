package com.padimasso.autocasting.application.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminUserSuspensionRequest(
    boolean suspended,
    @NotBlank(message = "validation.required")
    String reason
) {
}
