package com.padimasso.autocasting.application.admin.notes.dto.request;

import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntityType;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteSource;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteType;

import java.util.Map;
import java.util.UUID;

public record CreateAdminNoteCommand(
    AdminNoteEntityType entityType,
    UUID entityId,
    UUID relatedUserId,
    AdminNoteType noteType,
    AdminNoteSource source,
    String title,
    String body,
    Map<String, Object> metadata,
    boolean pinned
) {
}
