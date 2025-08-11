package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.MediaEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaRepository extends SoftDeleteRepository<MediaEntity, UUID> {
    Optional<MediaEntity> findByProfileId(UUID id);
}
