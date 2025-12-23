package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record CastingResponse(
    UUID id,
    String defaultCode,
    SiteMetadataObject castingStatus,
    CastingBasicInfoResponse basicInfoSection,
    CastingRolesSectionResponse rolesSection,
    CastingRequirementsSectionResponse actingSection,
    CastingRemunerationResponse remunerationSection
) {
}
