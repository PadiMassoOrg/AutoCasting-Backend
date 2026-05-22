package com.padimasso.autocasting.application.billing.repository;

import com.padimasso.autocasting.application.billing.model.BillingDiscountEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface BillingDiscountRepository extends SoftDeleteRepository<BillingDiscountEntity, UUID> {
    Optional<BillingDiscountEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);
}
