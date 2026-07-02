package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.common.model.EntityType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminHistoryRowResponse(
    UUID id,
    EntityType entityType,
    UUID entityId,
    String note,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy,
    boolean deleted
) {
}
