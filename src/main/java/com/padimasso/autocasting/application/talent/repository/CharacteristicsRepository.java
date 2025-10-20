package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.CharacteristicsEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface CharacteristicsRepository extends SoftDeleteRepository<CharacteristicsEntity, UUID> {
    Optional<CharacteristicsEntity> findByTalentProfileId(UUID id);
}
