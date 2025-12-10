package com.padimasso.autocasting.application.castings.dto.response;

import java.util.UUID;

public record CastingResponse(
    UUID id,
    CastingBasicInfoResponse basicInfo,
    CastingRolesResponse roles,
    CastingActingResponse acting,
    CastingRemunerationResponse remuneration
) {
}
