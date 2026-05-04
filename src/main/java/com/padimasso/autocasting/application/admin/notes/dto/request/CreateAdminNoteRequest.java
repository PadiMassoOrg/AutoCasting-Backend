package com.padimasso.autocasting.application.admin.notes.dto.request;

import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntityType;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record CreateAdminNoteRequest(
    @NotNull AdminNoteEntityType entityType,
    @NotNull UUID entityId,
    UUID relatedUserId,
    AdminNoteType noteType,
    String title,
    @NotBlank String body,
    Map<String, Object> metadata,
    Boolean pinned
) {
}
