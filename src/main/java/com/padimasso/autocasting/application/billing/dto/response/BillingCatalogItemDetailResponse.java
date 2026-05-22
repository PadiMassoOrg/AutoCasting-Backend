package com.padimasso.autocasting.application.billing.dto.response;

import com.padimasso.autocasting.application.billing.model.BillingAudience;
import com.padimasso.autocasting.application.billing.model.BillingType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record BillingCatalogItemDetailResponse(
    UUID id,
    String code,
    String stringCode,
    String description,
    Set<BillingAudience> audiences,
    BillingType billingType,
    boolean active,
    boolean deleted,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy,
    List<BillingCatalogPriceResponse> prices,
    List<BillableItemDiscountResponse> itemDiscounts
) {
}
