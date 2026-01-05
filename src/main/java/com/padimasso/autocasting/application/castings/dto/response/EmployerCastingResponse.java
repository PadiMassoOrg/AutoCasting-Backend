package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record EmployerCastingResponse(
    UUID id,
    String defaultCode,
    SiteMetadataObject castingStatus,
    UUID basicInfoSectionId,
    UUID rolesSectionId,
    UUID requirementsSectionId,
    UUID remunerationSectionId
) {
}
