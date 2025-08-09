package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.BasicInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BasicInfoRepository extends JpaRepository<BasicInfoEntity, UUID> {
    Optional<BasicInfoEntity> findByProfileId(UUID profileId);
}
