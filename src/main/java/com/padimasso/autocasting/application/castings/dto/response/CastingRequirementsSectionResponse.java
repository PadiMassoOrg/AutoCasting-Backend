package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
import java.util.UUID;

public record CastingRequirementsSectionResponse(
    UUID id,
    SiteMetadataObject sectionStatus,
    List<CastingRequirementResponse> requirements
) {
}
