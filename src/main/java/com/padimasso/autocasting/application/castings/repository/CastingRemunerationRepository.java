package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRemunerationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingRemunerationRepository extends SoftDeleteRepository<CastingRemunerationEntity, UUID> {
}
