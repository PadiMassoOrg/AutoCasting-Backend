package com.padimasso.autocasting.application.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record BillingCatalogPriceUpsertRequest(
    @NotBlank(message = "general.required_one")
    @Size(min = 3, max = 3, message = "general.invalid_parameter")
    String currencyCode,
    @NotNull(message = "general.required_one")
    @PositiveOrZero(message = "general.invalid_parameter")
    Long amountMinor,
    @NotNull(message = "general.required_one")
    OffsetDateTime validFrom,
    OffsetDateTime validTo,
    Boolean active
) {
}
