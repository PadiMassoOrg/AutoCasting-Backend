package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRoleCharacteristicsEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingRoleCharacteristicsRepository extends SoftDeleteRepository<CastingRoleCharacteristicsEntity, UUID> {
}
