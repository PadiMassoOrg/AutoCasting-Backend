package com.padimasso.autocasting.application.billing.dto.response;

import com.padimasso.autocasting.application.billing.model.BillableItemDiscountEntity;

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
    public static BillableItemDiscountResponse from(BillableItemDiscountEntity entity) {
        return new BillableItemDiscountResponse(
            entity.getId(),
            entity.getBillableItem().getId(),
            entity.getBillingDiscount().getId(),
            entity.getValidFrom(),
            entity.getValidTo(),
            entity.getPriority(),
            entity.isActive(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }
}
