package com.padimasso.autocasting.application.billing.repository;

import com.padimasso.autocasting.application.billing.model.BillableItemPriceEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface BillableItemPriceRepository extends SoftDeleteRepository<BillableItemPriceEntity, UUID> {

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM BillableItemPriceEntity p
        WHERE p.billableItem.id = :billableItemId
          AND p.currencyCode = :currencyCode
          AND (:excludeId IS NULL OR p.id <> :excludeId)
          AND (p.validTo IS NULL OR p.validTo > :validFrom)
          AND (:validTo IS NULL OR p.validFrom < :validTo)
        """)
    boolean existsOverlappingWindow(
        @Param("billableItemId") UUID billableItemId,
        @Param("currencyCode") String currencyCode,
        @Param("validFrom") OffsetDateTime validFrom,
        @Param("validTo") OffsetDateTime validTo,
        @Param("excludeId") UUID excludeId
    );
}
