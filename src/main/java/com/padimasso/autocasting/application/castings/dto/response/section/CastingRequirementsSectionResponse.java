package com.padimasso.autocasting.application.castings.dto.response.section;

import com.padimasso.autocasting.application.castings.dto.response.CastingRequirementResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CastingRequirementsSectionResponse(
    UUID id,
    SiteMetadataObject sectionStatus,
    List<CastingRequirementResponse> requirements,
    LocalDateTime modifiedAt
) {
}
