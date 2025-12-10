package com.padimasso.autocasting.application.castings.dto.response;

import java.util.UUID;

public record CastingActingRequirementResponse(
    UUID id,
    UUID castingRoleId,
    boolean isComplete,
    String description,
    Integer slotsCount
) {
}
