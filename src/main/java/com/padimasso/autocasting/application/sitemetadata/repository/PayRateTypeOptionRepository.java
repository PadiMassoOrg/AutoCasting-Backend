package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface PayRateTypeOptionRepository extends SoftDeleteRepository<PayRateTypeOptionEntity, UUID> {
    Optional<PayRateTypeOptionEntity> findByStringCode(String stringCode);
}
