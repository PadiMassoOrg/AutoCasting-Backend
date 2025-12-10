package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.CastingActingModeOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface CastingActingModeOptionRepository extends SoftDeleteRepository<CastingActingModeOptionEntity, UUID> {
    Optional<CastingActingModeOptionEntity> findByStringCode(String stringCode);
}
