package com.padimasso.autocasting.application.castings.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.UUID;

public record CastingRequirementBulkRequest(
    @NotNull(message = "casting.requirements_section_required")
    UUID requirementsSectionId,
    @NotEmpty(message = "casting.role_required")
    List<@NotNull(message = "casting.role_required") UUID> roleIds,
    @NotNull(message = "casting.requires_audio_required")
    Boolean requiresAudio,
    @NotNull(message = "casting.requires_video_required")
    Boolean requiresVideo,
    JsonNullable<String> description
) {
}
