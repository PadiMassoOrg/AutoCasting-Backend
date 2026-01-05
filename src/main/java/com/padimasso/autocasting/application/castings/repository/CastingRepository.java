package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.repository.projection.EmployerCastingDetailsProjection;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CastingRepository extends SoftDeleteRepository<CastingEntity, UUID> {

    @Query("""
        select
            c.id as id,
            c.defaultCode as defaultCode,
            s as status,
            bi.id as basicInfoSectionId,
            rs.id as rolesSectionId,
            req.id as requirementsSectionId,
            rem.id as remunerationSectionId
        from CastingEntity c
            left join c.status s
            left join c.basicInfo bi
            left join c.roles rs
            left join c.requirements req
            left join c.remuneration rem
        where c.defaultCode = :slug
          and c.deleted = false
        """)
    Optional<EmployerCastingDetailsProjection> findDetailsProjectionBySlug(@Param("slug") String slug);

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
        "roles.roles.remuneration",

        "requirements",

        "remuneration",
    })
    Page<CastingEntity> findAll(@Nullable Specification<CastingEntity> spec, Pageable pageable);

}
