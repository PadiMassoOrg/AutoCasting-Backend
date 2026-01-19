package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRequirementsSectionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingRequirementsSectionRepository extends SoftDeleteRepository<CastingRequirementsSectionEntity, UUID> {
}
