package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRemunerationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface CastingRemunerationsSectionRepository extends SoftDeleteRepository<CastingRemunerationEntity, UUID> {

    Optional<CastingRemunerationEntity> findByCastingIdAndDeletedFalse(UUID castingId);
    
}
