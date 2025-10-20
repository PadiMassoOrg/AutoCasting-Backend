package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.SocialMediaEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface SocialMediaRepository extends SoftDeleteRepository<SocialMediaEntity, UUID> {
    Optional<SocialMediaEntity> findByTalentProfileId(UUID id);
}
