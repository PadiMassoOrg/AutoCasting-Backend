package com.padimasso.autocasting.application.castings.dto.response;

import java.util.UUID;

public record CastingRequirementResponse(
    UUID id,
    UUID roleId,
    String description,
    boolean requiresAudio,
    boolean requiresVideo
) {
}
