package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.EducationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface EducationRepository extends SoftDeleteRepository<EducationEntity, UUID> {
    default List<EducationEntity> findAllByProfileId(UUID id) {
        return findAllByPropertyEquals("profile.id", id);
    }

    // Si un día necesitas admin (incluye borrados):
    default List<EducationEntity> findAllByProfileIdIncludingDeleted(UUID id) {
        return findAllIncludingDeletedByPropertyEquals("profile.id", id);
    }
}
