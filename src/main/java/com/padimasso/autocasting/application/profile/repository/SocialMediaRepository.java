package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.SocialMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SocialMediaRepository extends JpaRepository<SocialMediaEntity, UUID> {
    Optional<SocialMediaEntity> findByProfileId(UUID profileId);
}
