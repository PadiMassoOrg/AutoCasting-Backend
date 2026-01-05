package com.padimasso.autocasting.application.castings.dto.request.section;

import java.util.UUID;

public record CastingRemunerationsSectionPatchRequest(
    UUID id,
    UUID castingCompensationTypeId
) {
}
