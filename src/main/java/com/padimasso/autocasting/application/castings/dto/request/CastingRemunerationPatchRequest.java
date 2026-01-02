package com.padimasso.autocasting.application.castings.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.util.UUID;

public record CastingRemunerationPatchRequest(
    UUID id,
    JsonNullable<UUID> payRateTypeId,
    JsonNullable<UUID> currencyId,
    JsonNullable<BigDecimal> amount,
    JsonNullable<String> notes
) {
}
