package com.padimasso.autocasting.application.billing.dto.response;

import com.padimasso.autocasting.application.billing.model.BillingAudience;
import com.padimasso.autocasting.application.billing.model.BillingType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record BillingCatalogItemListResponse(
    UUID id,
    String code,
    String stringCode,
    Set<BillingAudience> audiences,
    BillingType billingType,
    boolean active,
    String currentPriceCurrencyCode,
    Long currentPriceAmountMinor,
    OffsetDateTime currentPriceValidFrom,
    OffsetDateTime currentPriceValidTo,
    LocalDateTime modifiedAt
) {
}
