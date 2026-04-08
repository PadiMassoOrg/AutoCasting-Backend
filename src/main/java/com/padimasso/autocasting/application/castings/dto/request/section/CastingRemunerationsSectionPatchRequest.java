package com.padimasso.autocasting.application.castings.dto.request.section;

import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

public record CastingRemunerationsSectionPatchRequest(
    @NotNull(message = "casting.remuneration_section_id_required")
    UUID id,
    @NotNull(message = "casting.compensation_type_required")
    UUID castingCompensationTypeId,
    JsonNullable<String> notes
) {
}
