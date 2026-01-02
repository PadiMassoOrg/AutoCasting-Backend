package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CastingRoleRepository extends SoftDeleteRepository<CastingRoleEntity, UUID> {
    @Override
    @EntityGraph(attributePaths = {
        // Sección y casting padre
        "rolesSection",
        "rolesSection.casting",
        "rolesSection.casting.basicInfo",
        "rolesSection.casting.basicInfo.projectType",
        "rolesSection.casting.basicInfo.castingModality",
        "rolesSection.casting.employerProfile",
        "rolesSection.casting.employerProfile.basicInfo",

        // Metadata propia del rol
        "professions",
        "roleType",
        "gender",
        "skills",
        "remuneration",

        // Subentidades del rol
        "characteristics",
        "characteristics.hairColor",
        "characteristics.eyeColor",
        "characteristics.ethnicity",
        "characteristics.dietOption",

        "remuneration",
        "remuneration.currency",
        "remuneration.payRateType"
    })
    Page<CastingRoleEntity> findAll(@Nullable Specification<CastingRoleEntity> spec, Pageable pageable);

    List<CastingRoleEntity> findAllByRolesSection_Casting_IdAndIdInAndDeletedFalse(
        UUID castingId,
        Collection<UUID> roleIds
    );
}
