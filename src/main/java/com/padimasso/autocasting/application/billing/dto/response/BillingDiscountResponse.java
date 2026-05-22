package com.padimasso.autocasting.application.billing.dto.response;

import com.padimasso.autocasting.application.billing.model.BillingDiscountEntity;
import com.padimasso.autocasting.application.billing.model.BillingDiscountType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BillingDiscountResponse(
    UUID id,
    String code,
    String stringCode,
    String description,
    BillingDiscountType discountType,
    Integer percentageBps,
    Long amountMinor,
    String currencyCode,
    boolean stackable,
    boolean active,
    OffsetDateTime startsAt,
    OffsetDateTime endsAt,
    boolean deleted,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
    public static BillingDiscountResponse from(BillingDiscountEntity entity) {
        return new BillingDiscountResponse(
            entity.getId(),
            entity.getCode(),
            entity.getStringCode(),
            entity.getDescription(),
            entity.getDiscountType(),
            entity.getPercentageBps(),
            entity.getAmountMinor(),
            entity.getCurrencyCode(),
            entity.isStackable(),
            entity.isActive(),
            entity.getStartsAt(),
            entity.getEndsAt(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }
}
