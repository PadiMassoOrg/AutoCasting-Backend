package com.padimasso.autocasting.application.billing.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BillingCatalogPriceResponse(
    UUID id,
    String currencyCode,
    long amountMinor,
    String amountDisplay,
    OffsetDateTime validFrom,
    OffsetDateTime validTo,
    boolean active
) {
}
