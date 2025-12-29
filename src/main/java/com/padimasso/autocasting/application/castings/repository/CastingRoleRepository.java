package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.List;
import java.util.UUID;

public interface CastingRoleRepository extends SoftDeleteRepository<CastingRoleEntity, UUID> {
    List<CastingRoleEntity> findAllByRolesSection_IdOrderByCreatedAtDesc(UUID sectionId);
}
