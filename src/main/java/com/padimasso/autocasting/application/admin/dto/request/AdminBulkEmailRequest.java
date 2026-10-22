package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.email.model.EmailType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AdminBulkEmailRequest(
    @NotNull(message = "validation.required")
    EmailType emailType,
    @NotEmpty(message = "general.ids_required")
    List<UUID> userIds,
    boolean forceResend
) {
}
