package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.CastingApplicationStatusOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface CastingApplicationStatusOptionRepository extends SoftDeleteRepository<CastingApplicationStatusOptionEntity, UUID> {
    Optional<CastingApplicationStatusOptionEntity> findByStringCode(String stringCode);
}
