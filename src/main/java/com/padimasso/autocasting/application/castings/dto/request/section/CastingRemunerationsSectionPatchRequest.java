package com.padimasso.autocasting.application.castings.dto.request.section;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

public record CastingRemunerationsSectionPatchRequest(
    UUID id,
    UUID castingCompensationTypeId,
    JsonNullable<String> notes
) {
}
