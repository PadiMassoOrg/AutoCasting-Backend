package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.ContactEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends SoftDeleteRepository<ContactEntity, UUID> {
    Optional<ContactEntity> findByTalentProfileId(UUID profileId);

}
