package com.padimasso.autocasting.application.billing.dto.response;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BillableItemDiscountResponse(
    UUID id,
    UUID billableItemId,
    UUID billingDiscountId,
    OffsetDateTime validFrom,
    OffsetDateTime validTo,
    short priority,
    boolean active,
    boolean deleted,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
}
