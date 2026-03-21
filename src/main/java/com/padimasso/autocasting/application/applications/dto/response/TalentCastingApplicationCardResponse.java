package com.padimasso.autocasting.application.applications.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

public record TalentCastingApplicationCardResponse(
    String roleName,
    String castingName,
    SiteMetadataObject castingProjectType,
    SiteMetadataObject castingModality,
    SiteMetadataObject castingStatus,
    String castingRoleId,
    String castingSlug
) {
}
