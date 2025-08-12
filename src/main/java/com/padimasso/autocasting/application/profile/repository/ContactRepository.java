package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.ContactEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends SoftDeleteRepository<ContactEntity, UUID> {
    Optional<ContactEntity> findByProfileId(UUID profileId);

}
