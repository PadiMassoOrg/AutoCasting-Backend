package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;

public interface CastingRepository extends SoftDeleteRepository<CastingEntity, UUID> {
    Optional<CastingEntity> findByDefaultCode(String slug);

    @Override
    @EntityGraph(attributePaths = {
        // Info básica del casting
        "basicInfo",
        "basicInfo.projectType",
        "basicInfo.castingModality",

        // Employer
        "employerProfile",
        "employerProfile.basicInfo",

        // Sección de roles + roles + metadata de roles
        "roles",
        "roles.roles",
        "roles.roles.professions",
        "roles.roles.roleType",
        "roles.roles.gender",
        "roles.roles.skills",

        // Si quieres, también remuneration / acting para futuras vistas
        "remuneration",
        "requirements"
    })
    Page<CastingEntity> findAll(@Nullable Specification<CastingEntity> spec, Pageable pageable);

}
