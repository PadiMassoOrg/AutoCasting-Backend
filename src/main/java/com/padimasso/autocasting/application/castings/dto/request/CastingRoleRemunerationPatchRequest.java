package com.padimasso.autocasting.application.castings.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.util.UUID;

public record CastingRoleRemunerationPatchRequest(
    UUID id,
    UUID payRateTypeId,
    UUID currencyId,
    JsonNullable<BigDecimal> amount,
    JsonNullable<String> notes
) {
}
