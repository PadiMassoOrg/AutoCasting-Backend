package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.projection.CastingRoleKeyProjection;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CastingRoleRepository extends SoftDeleteRepository<CastingRoleEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {
        "casting",
        "casting.projectType",
        "casting.castingModality",
        "casting.status",
        "casting.employerProfile",
        "casting.employerProfile.basicInfo",
        "professions",
        "roleType",
        "gender",
        "skills",
        "payRateType",
        "currency",
        "ethnicity"
    })
    Page<CastingRoleEntity> findAll(@Nullable Specification<CastingRoleEntity> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
        "casting",
        "casting.projectType",
        "casting.castingModality",
        "casting.status",
        "casting.employerProfile",
        "casting.employerProfile.basicInfo",
        "professions",
        "roleType",
        "gender",
        "skills",
        "payRateType",
        "currency",
        "ethnicity"
    })
    Optional<CastingRoleEntity> findByIdAndDeletedFalse(UUID roleId);

    @EntityGraph(attributePaths = {
        "casting",
        "casting.projectType",
        "casting.castingModality",
        "professions",
        "roleType",
        "gender",
        "skills",
        "payRateType",
        "currency",
        "ethnicity"
    })
    List<CastingRoleEntity> findAllByCasting_IdAndDeletedFalse(UUID castingId);

    List<CastingRoleEntity> findAllByCasting_IdAndIdInAndDeletedFalse(UUID castingId, Collection<UUID> roleIds);

    long countByCasting_IdAndDeletedFalse(UUID castingId);

    @Query("""
        select
            r.id as roleId,
            r.roleName as roleName
        from CastingRoleEntity r
        where r.deleted = false
          and r.casting.deleted = false
          and r.casting.defaultCode = :slug
          and r.casting.employerProfile.id = :employerProfileId
        order by lower(r.roleName) asc, r.id asc
        """)
    List<CastingRoleKeyProjection> findRoleKeysByCastingSlugAndEmployer(
        @Param("slug") String slug,
        @Param("employerProfileId") UUID employerProfileId
    );
}
