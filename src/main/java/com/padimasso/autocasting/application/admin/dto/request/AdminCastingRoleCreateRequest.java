package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Adds a role to the casting named by {@code role.castingId}; the reason is stored in the history entry. */
public record AdminCastingRoleCreateRequest(
    @NotBlank(message = "validation.required")
    String reason,
    @NotNull(message = "validation.required") @Valid CastingRoleRequest role
) {
}
