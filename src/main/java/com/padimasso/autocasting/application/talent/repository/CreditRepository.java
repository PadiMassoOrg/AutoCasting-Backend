package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.CreditEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface CreditRepository extends SoftDeleteRepository<CreditEntity, UUID> {
    default List<CreditEntity> findAllByTalentProfileId(UUID id) {
        return findAllByPropertyEquals("talent.id", id);
    }

    // Si un día necesitas admin (incluye borrados):
    default List<CreditEntity> findAllByTalentProfileIdIncludingDeleted(UUID id) {
        return findAllIncludingDeletedByPropertyEquals("talent.id", id);
    }
}
