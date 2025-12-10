package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record CastingRoleRemunerationResponse(
    UUID id,
    UUID castingRoleId,
    Boolean isComplete,
    SiteMetadataObject payRateType,
    SiteMetadataObject currency,
    Long amount,
    String notes
) {
}
