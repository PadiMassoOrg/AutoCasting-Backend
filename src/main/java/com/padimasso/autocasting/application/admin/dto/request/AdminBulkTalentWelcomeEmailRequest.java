package com.padimasso.autocasting.application.admin.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record AdminBulkTalentWelcomeEmailRequest(
    @NotEmpty(message = "general.ids_required")
    List<UUID> userIds
) {
}
