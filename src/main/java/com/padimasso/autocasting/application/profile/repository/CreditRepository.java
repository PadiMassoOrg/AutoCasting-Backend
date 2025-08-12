package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.CreditEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface CreditRepository extends SoftDeleteRepository<CreditEntity, UUID> {
    default List<CreditEntity> findAllByProfileId(UUID id) {
        return findAllByPropertyEquals("profile.id", id);
    }

    // Si un día necesitas admin (incluye borrados):
    default List<CreditEntity> findAllByProfileIdIncludingDeleted(UUID id) {
        return findAllIncludingDeletedByPropertyEquals("profile.id", id);
    }
}
