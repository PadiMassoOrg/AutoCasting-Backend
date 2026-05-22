package com.padimasso.autocasting.application.billing.repository;

import com.padimasso.autocasting.application.billing.model.BillableItemEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface BillableItemRepository extends SoftDeleteRepository<BillableItemEntity, UUID> {
    Optional<BillableItemEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);
}
