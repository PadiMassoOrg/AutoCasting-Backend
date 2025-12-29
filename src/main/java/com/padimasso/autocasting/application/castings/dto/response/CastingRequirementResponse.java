package com.padimasso.autocasting.application.castings.dto.response;

import java.util.UUID;

public record CastingRequirementResponse(
    UUID id,
    UUID castingRoleId,
    String description,
    boolean requiresAudio,
    boolean requiresVideo
) {
}
