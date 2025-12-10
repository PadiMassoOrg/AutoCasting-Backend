package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingRoleRepository extends SoftDeleteRepository<CastingRoleEntity, UUID> {
}
