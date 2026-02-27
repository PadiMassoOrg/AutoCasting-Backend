package com.padimasso.autocasting.application.applications.repository;

import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingApplicationRepository extends SoftDeleteRepository<CastingApplicationEntity, UUID> {
    boolean existsByCastingRoleIdAndTalentProfileId(UUID roleId, UUID id);
}
