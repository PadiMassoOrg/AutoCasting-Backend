package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<ContactEntity, UUID> {
    Optional<ContactEntity> findByProfileId(UUID profileId);
}
