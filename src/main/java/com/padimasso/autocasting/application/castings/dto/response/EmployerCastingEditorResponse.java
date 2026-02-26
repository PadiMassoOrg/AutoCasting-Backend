package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record EmployerCastingEditorResponse(
    UUID id,
    String defaultCode,
    SiteMetadataObject castingStatus,
    UUID basicInfoSectionId,
    UUID rolesSectionId,
    UUID requirementsSectionId,
    UUID remunerationSectionId,
    SiteMetadataObject basicInfoSectionStatus,
    SiteMetadataObject rolesSectionStatus,
    SiteMetadataObject requirementsSectionStatus,
    SiteMetadataObject remunerationSectionStatus,
    boolean publishable
) {
}
