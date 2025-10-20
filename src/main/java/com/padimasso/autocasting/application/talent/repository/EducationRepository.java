package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.EducationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface EducationRepository extends SoftDeleteRepository<EducationEntity, UUID> {
    default List<EducationEntity> findAllByTalentProfileId(UUID id) {
        return findAllByPropertyEquals("talent.id", id);
    }

    // Si un día necesitas admin (incluye borrados):
    default List<EducationEntity> findAllByTalentProfileIdIncludingDeleted(UUID id) {
        return findAllIncludingDeletedByPropertyEquals("talent.id", id);
    }
}
