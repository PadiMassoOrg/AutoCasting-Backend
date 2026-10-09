package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.common.model.EntityType;

import java.time.LocalDateTime;
import java.util.UUID;

/** {@code entityLabel} is the role's name for role entries (deleted roles included), and null for the casting. */
public record AdminCastingHistoryRowResponse(
    UUID id,
    EntityType entityType,
    UUID entityId,
    String entityLabel,
    String note,
    String changes,
    LocalDateTime createdAt
) {
}
