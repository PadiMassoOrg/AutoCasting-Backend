package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Replaces a casting role on behalf of an admin; the reason is stored in the history entry. */
public record AdminCastingRoleUpdateRequest(
    @NotBlank(message = "validation.required")
    String reason,
    @NotNull(message = "validation.required") @Valid CastingRoleRequest role
) {
}
