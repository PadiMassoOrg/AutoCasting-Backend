package com.padimasso.autocasting.application.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** The copy keeps the source role's name when {@code roleName} is blank. */
public record AdminCastingRoleDuplicateRequest(
    @NotBlank(message = "validation.required")
    String reason,
    @Size(max = 255, message = "casting.role_name_max_length")
    String roleName
) {
}
