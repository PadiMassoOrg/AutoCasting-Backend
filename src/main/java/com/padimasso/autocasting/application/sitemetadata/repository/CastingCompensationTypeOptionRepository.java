package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.CastingCompensationTypeOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface CastingCompensationTypeOptionRepository extends SoftDeleteRepository<CastingCompensationTypeOptionEntity, UUID> {
    Optional<CastingCompensationTypeOptionEntity> findByStringCode(String stringCode);
}
