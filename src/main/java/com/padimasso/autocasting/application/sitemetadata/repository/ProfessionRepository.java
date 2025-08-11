package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.ProfessionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProfessionRepository extends SoftDeleteRepository<ProfessionEntity, UUID> {

    List<ProfessionEntity> findAllByIdIn(Set<UUID> ids);
}
