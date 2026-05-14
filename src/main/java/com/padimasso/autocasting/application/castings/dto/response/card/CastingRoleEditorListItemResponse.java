package com.padimasso.autocasting.application.castings.dto.response.card;

import java.time.LocalDateTime;
import java.util.UUID;

public record CastingRoleEditorListItemResponse(
    UUID id,
    String roleName,
    LocalDateTime modifiedAt
) {
}
