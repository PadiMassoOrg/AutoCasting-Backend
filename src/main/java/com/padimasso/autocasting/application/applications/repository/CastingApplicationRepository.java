package com.padimasso.autocasting.application.applications.repository;

import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.application.applications.repository.projection.ApplicationProfessionProjection;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CastingApplicationRepository
    extends SoftDeleteRepository<CastingApplicationEntity, UUID>,
    JpaSpecificationExecutor<CastingApplicationEntity> {

    // ===== Talent =====
    boolean existsByCastingRoleIdAndTalentProfileId(UUID roleId, UUID id);

    @Query("""
        select (count(a) > 0)
        from CastingApplicationEntity a
        where a.deleted = false
          and a.talentProfile.id = :talentProfileId
          and a.castingRole.id = :roleId
        """)
    boolean existsByTalentProfileIdAndRoleId(
        @Param("talentProfileId") UUID talentProfileId,
        @Param("roleId") UUID roleId
    );

    @Override
    @EntityGraph(attributePaths = {
        // Application
        "status",

        // Role + Casting
        "castingRole",
        "castingRole.rolesSection",
        "castingRole.rolesSection.casting",
        "castingRole.rolesSection.casting.basicInfo",
        "castingRole.rolesSection.casting.basicInfo.projectType",
        "castingRole.rolesSection.casting.basicInfo.castingModality",
        "castingRole.rolesSection.casting.status",

        // Talent
        "talentProfile",
        "talentProfile.basicInfo",
        "talentProfile.basicInfo.professions",
        "talentProfile.media"
    })
    Page<CastingApplicationEntity> findAll(@Nullable Specification<CastingApplicationEntity> spec, Pageable pageable);

    // ===== Employer =====
    @Query("""
        select
            a.id as applicationId,
            p.id as professionId,
            p.stringCode as professionCode,
            p.categoryStringCode as professionCategoryStringCode
        from CastingApplicationEntity a
            join a.talentProfile tp
            left join tp.basicInfo bi
            left join bi.professions p
        where a.deleted = false
          and a.id in :applicationIds
        """)
    List<ApplicationProfessionProjection> findProfessionsByApplicationIds(
        @Param("applicationIds") List<UUID> applicationIds
    );
}
