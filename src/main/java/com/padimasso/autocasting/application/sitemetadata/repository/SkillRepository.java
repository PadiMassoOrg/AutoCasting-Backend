package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.SkillEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Set;
import java.util.UUID;

public interface SkillRepository extends SoftDeleteRepository<SkillEntity, UUID> {

    Set<SkillEntity> findAllByIdIn(Set<UUID> ids);
}
