package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CastingRoleRemunerationRowResponse(
    UUID id,
    UUID castingRoleId,
    String roleName,
    boolean isComplete,
    SiteMetadataObject payRateType,
    SiteMetadataObject currency,
    BigDecimal amount,
    String notes,
    LocalDateTime modifiedAt
) {
}
