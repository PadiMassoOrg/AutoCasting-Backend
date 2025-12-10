package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingRepository extends SoftDeleteRepository<CastingEntity, UUID> {
}
