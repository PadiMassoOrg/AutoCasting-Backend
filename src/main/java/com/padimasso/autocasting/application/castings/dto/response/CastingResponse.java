package com.padimasso.autocasting.application.castings.dto.response;

import java.util.UUID;

public record CastingResponse(
    UUID id,
    CastingBasicInfoResponse basicInfoSection,
    CastingRolesSectionResponse rolesSection,
    CastingActingResponse actingSection,
    CastingRemunerationResponse remunerationSection
) {
}
