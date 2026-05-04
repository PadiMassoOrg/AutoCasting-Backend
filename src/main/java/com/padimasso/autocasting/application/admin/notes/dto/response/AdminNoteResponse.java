package com.padimasso.autocasting.application.admin.notes.dto.response;

import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntity;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntityType;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteSource;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AdminNoteResponse(
    UUID id,
    AdminNoteEntityType entityType,
    UUID entityId,
    UUID relatedUserId,
    UUID authorUserId,
    String authorEmail,
    AdminNoteType noteType,
    AdminNoteSource source,
    String title,
    String body,
    Map<String, Object> metadata,
    boolean pinned,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
    public static AdminNoteResponse from(AdminNoteEntity note) {
        return new AdminNoteResponse(
            note.getId(),
            note.getEntityType(),
            note.getEntityId(),
            note.getRelatedUser() != null ? note.getRelatedUser().getId() : null,
            note.getAuthorUser().getId(),
            note.getAuthorUser().getEmail(),
            note.getNoteType(),
            note.getSource(),
            note.getTitle(),
            note.getBody(),
            note.getMetadata(),
            note.isPinned(),
            note.getCreatedAt(),
            note.getCreatedBy(),
            note.getModifiedAt(),
            note.getModifiedBy()
        );
    }
}
