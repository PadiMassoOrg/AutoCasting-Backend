package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
import java.util.UUID;

public record CastingRemunerationResponse(
    UUID id,
    SiteMetadataObject sectionStatus,
    SiteMetadataObject compensationType,
    boolean paySameForAllRoles,
    List<CastingRoleRemunerationResponse> remunerations
) {
}
