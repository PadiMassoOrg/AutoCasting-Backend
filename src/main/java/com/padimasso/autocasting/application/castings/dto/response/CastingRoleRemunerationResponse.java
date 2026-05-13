package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public record CastingRoleRemunerationResponse(
    boolean isComplete,
    SiteMetadataObject payRateType,
    SiteMetadataObject currency,
    BigDecimal amount,
    String notes,
    LocalDateTime modifiedAt
) {
}
