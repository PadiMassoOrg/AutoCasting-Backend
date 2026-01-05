package com.padimasso.autocasting.application.castings.dto.response.section;

import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationRowResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
import java.util.UUID;

public record CastingRemunerationsSectionResponse(
    UUID id,
    SiteMetadataObject sectionStatus,
    SiteMetadataObject compensationType,
    boolean paySameForAllRoles,
    List<CastingRoleRemunerationRowResponse> remunerations
) {
}
