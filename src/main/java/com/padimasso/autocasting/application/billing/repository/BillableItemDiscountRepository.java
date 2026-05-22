package com.padimasso.autocasting.application.billing.repository;

import com.padimasso.autocasting.application.billing.model.BillableItemDiscountEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface BillableItemDiscountRepository extends SoftDeleteRepository<BillableItemDiscountEntity, UUID> {

    @Query("""
        SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
        FROM BillableItemDiscountEntity d
        WHERE d.billableItem.id = :billableItemId
          AND d.billingDiscount.id = :billingDiscountId
          AND (:excludeId IS NULL OR d.id <> :excludeId)
          AND (d.validTo IS NULL OR d.validTo > :validFrom)
          AND (:validTo IS NULL OR d.validFrom < :validTo)
        """)
    boolean existsOverlappingWindow(
        @Param("billableItemId") UUID billableItemId,
        @Param("billingDiscountId") UUID billingDiscountId,
        @Param("validFrom") OffsetDateTime validFrom,
        @Param("validTo") OffsetDateTime validTo,
        @Param("excludeId") UUID excludeId
    );
}
