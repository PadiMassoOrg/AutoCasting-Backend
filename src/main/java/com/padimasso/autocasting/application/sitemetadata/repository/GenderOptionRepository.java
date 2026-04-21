package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface GenderOptionRepository extends SoftDeleteRepository<GenderOptionEntity, UUID> {
    Optional<GenderOptionEntity> findByStringCode(String stringCode);

}
