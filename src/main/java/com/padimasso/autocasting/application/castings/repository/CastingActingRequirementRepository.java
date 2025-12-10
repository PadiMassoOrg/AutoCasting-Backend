package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingActingRequirementEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingActingRequirementRepository extends SoftDeleteRepository<CastingActingRequirementEntity, UUID> {
}
