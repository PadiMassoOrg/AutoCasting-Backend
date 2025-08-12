package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends SoftDeleteRepository<ProfileEntity, UUID> {
    Optional<ProfileEntity> findByUserId(UUID id);

    Optional<ProfileEntity> findByDefaultSlugOrPremiumSlug(String slug, String slug1);
}
