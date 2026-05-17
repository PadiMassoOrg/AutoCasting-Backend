package com.padimasso.autocasting.application.castings.dto.response;

import java.util.List;
import java.util.UUID;

public record PublicCastingOverviewResponse(
    PublicCastingResponse casting,
    List<UUID> appliedRoleIds
) {
}
