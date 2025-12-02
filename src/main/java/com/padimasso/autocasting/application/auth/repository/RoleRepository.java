package com.padimasso.autocasting.application.auth.repository;

import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends SoftDeleteRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByCode(String code);

    Optional<Set<RoleEntity>> findAllByCodeIn(List<String> talent);
}
