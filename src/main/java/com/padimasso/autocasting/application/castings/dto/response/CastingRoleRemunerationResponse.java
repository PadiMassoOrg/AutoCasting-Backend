package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.math.BigDecimal;
import java.util.UUID;

public record CastingRoleRemunerationResponse(
    UUID id,
    UUID castingRoleId,
    boolean isComplete,
    SiteMetadataObject payRateType,
    SiteMetadataObject currency,
    BigDecimal amount,
    String notes
) {
}
