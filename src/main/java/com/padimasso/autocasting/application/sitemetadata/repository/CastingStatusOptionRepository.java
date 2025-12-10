package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface CastingStatusOptionRepository extends SoftDeleteRepository<CastingStatusOptionEntity, UUID> {
    Optional<CastingStatusOptionEntity> findByStringCode(String stringCode);
}
