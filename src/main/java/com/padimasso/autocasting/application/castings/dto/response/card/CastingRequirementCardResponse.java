package com.padimasso.autocasting.application.castings.dto.response.card;

import java.time.LocalDateTime;
import java.util.UUID;

public record CastingRequirementCardResponse(
    UUID id,
    UUID roleId,
    String roleName,
    boolean requiresAudio,
    boolean requiresVideo,
    String description,
    LocalDateTime modifiedAt
) {
}
