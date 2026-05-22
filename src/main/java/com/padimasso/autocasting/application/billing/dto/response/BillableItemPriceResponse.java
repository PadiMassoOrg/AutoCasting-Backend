package com.padimasso.autocasting.application.billing.dto.response;

import com.padimasso.autocasting.application.billing.model.BillableItemPriceEntity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BillableItemPriceResponse(
    UUID id,
    UUID billableItemId,
    String currencyCode,
    long amountMinor,
    OffsetDateTime validFrom,
    OffsetDateTime validTo,
    boolean active,
    boolean deleted,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
    public static BillableItemPriceResponse from(BillableItemPriceEntity entity) {
        return new BillableItemPriceResponse(
            entity.getId(),
            entity.getBillableItem().getId(),
            entity.getCurrencyCode(),
            entity.getAmountMinor(),
            entity.getValidFrom(),
            entity.getValidTo(),
            entity.isActive(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }
}
