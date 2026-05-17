package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.math.BigDecimal;

public record PublicCastingRoleRemunerationResponse(
    boolean isComplete,
    SiteMetadataObject payRateType,
    SiteMetadataObject currency,
    BigDecimal amount,
    String notes
) {
}
