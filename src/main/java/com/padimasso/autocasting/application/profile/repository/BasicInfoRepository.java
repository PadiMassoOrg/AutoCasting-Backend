package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.BasicInfoEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface BasicInfoRepository extends SoftDeleteRepository<BasicInfoEntity, UUID> {
    Optional<BasicInfoEntity> findByProfileId(UUID profileId);
}
