package com.padimasso.autocasting.repository;

import com.padimasso.autocasting.model.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
    Optional<ProfileEntity> findByUserId(UUID id);

    boolean existsByUserId(UUID id);

    Optional<ProfileEntity> findByDefaultSlugOrPremiumSlug(String slug, String slug1);
}
