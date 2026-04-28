package com.padimasso.autocasting.application.applications.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record BulkCastingApplicationStatusRequest(
    @NotEmpty(message = "applications.ids_required")
    List<UUID> applicationIds,
    @NotBlank(message = "applications.status_required")
    String applicationStatus
) {
}
