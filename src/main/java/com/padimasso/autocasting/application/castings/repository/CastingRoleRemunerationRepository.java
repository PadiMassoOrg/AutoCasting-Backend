package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRoleRemunerationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingRoleRemunerationRepository extends SoftDeleteRepository<CastingRoleRemunerationEntity, UUID> {
}
