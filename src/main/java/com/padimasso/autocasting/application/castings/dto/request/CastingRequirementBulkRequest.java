package com.padimasso.autocasting.application.castings.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.UUID;

public record CastingRequirementBulkRequest(
    @NotNull UUID requirementsSectionId,
    @NotEmpty List<UUID> roleIds,
    @NotNull Boolean requiresAudio,
    @NotNull Boolean requiresVideo,
    JsonNullable<String> description
) {
}
