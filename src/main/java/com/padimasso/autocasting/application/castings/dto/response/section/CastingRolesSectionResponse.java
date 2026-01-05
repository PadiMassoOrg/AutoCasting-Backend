package com.padimasso.autocasting.application.castings.dto.response.section;

import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
import java.util.UUID;

public record CastingRolesSectionResponse(
    UUID id,
    SiteMetadataObject sectionStatus,
    String generalNotes,
    List<CastingRoleResponse> roles
) {
}
