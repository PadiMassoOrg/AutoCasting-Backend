package com.padimasso.autocasting.application.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

/** {@code status} is the string code of the target casting status. */
public record AdminCastingStatusRequest(
    @NotBlank(message = "validation.required")
    String reason,
    @NotBlank(message = "validation.required")
    String status
) {
}
