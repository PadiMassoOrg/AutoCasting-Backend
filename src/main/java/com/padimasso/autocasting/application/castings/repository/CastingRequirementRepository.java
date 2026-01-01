package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRequirementEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CastingRequirementRepository extends SoftDeleteRepository<CastingRequirementEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {
        "castingRole",
        "castingRequirementsSection",
        "castingRequirementsSection.casting"
    })
    Page<CastingRequirementEntity> findAll(@Nullable Specification<CastingRequirementEntity> spec, Pageable pageable);

    List<CastingRequirementEntity> findAllByCastingRequirementsSection_IdAndCastingRole_IdInAndDeletedFalse(
        UUID sectionId,
        Collection<UUID> roleIds
    );
}
