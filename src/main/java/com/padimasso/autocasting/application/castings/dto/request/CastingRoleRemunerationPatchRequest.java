package com.padimasso.autocasting.application.castings.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.util.UUID;

public record CastingRoleRemunerationPatchRequest(
    @NotNull(message = "casting.role_remuneration_id_required")
    UUID id,
    UUID payRateTypeId,
    UUID currencyId,
    @Digits(integer = 10, fraction = 2, message = "casting.amount_invalid")
    JsonNullable<BigDecimal> amount,
    JsonNullable<String> notes
) {
}
