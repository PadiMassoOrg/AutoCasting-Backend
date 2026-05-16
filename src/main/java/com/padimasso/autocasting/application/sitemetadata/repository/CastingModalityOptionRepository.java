package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface CastingModalityOptionRepository extends SoftDeleteRepository<CastingModalityOptionEntity, UUID> {
    Optional<CastingModalityOptionEntity> findByStringCode(String stringCode);
}
