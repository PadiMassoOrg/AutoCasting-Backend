package com.padimasso.autocasting.application.billing.dto.response;

import com.padimasso.autocasting.application.billing.model.BillableItemEntity;
import com.padimasso.autocasting.application.billing.model.BillingAudience;
import com.padimasso.autocasting.application.billing.model.BillingType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record BillableItemResponse(
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
    String modifiedBy
) {
    public static BillableItemResponse from(BillableItemEntity entity) {
        return new BillableItemResponse(
            entity.getId(),
            entity.getCode(),
            entity.getStringCode(),
            entity.getDescription(),
            entity.getAudiences(),
            entity.getBillingType(),
            entity.isActive(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }
}
