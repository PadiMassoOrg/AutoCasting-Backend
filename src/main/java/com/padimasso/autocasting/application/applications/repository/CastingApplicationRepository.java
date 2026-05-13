package com.padimasso.autocasting.application.applications.repository;

import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.application.applications.repository.projection.ApplicationProfessionProjection;
import com.padimasso.autocasting.application.sitemetadata.model.CastingApplicationStatusOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("""
        select ca.castingRole.id
        from CastingApplicationEntity ca
        where ca.deleted = false
          and ca.talentProfile.id = :talentProfileId
          and ca.castingRole.casting.id = :castingId
        """)
    List<UUID> findAppliedRoleIdsByTalentProfileIdAndCastingId(
        @Param("talentProfileId") UUID talentProfileId,
        @Param("castingId") UUID castingId
    );

    @Override
    Page<CastingApplicationEntity> findAll(@Nullable Specification<CastingApplicationEntity> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
        // Application
        "status",

        // Role + Casting
        "castingRole",
        "castingRole.gender",
        "castingRole.professions",
        "castingRole.roleType",
        "castingRole.casting",
        "castingRole.casting.projectType",
        "castingRole.casting.castingModality",
        "castingRole.casting.status",
        "castingRole.casting.employerProfile",
        "castingRole.casting.employerProfile.basicInfo"
    })
    @Query("""
        select distinct a
        from CastingApplicationEntity a
        where a.id in :ids
        """)
    List<CastingApplicationEntity> findAllForTalentCardsByIdIn(
        @Param("ids") List<UUID> ids
    );

    // ===== Employer =====
    @EntityGraph(attributePaths = {
        // Application
        "status",

        // Role + Casting
        "castingRole",
        "castingRole.casting",
        "castingRole.casting.projectType",
        "castingRole.casting.castingModality",

        // Talent
        "talentProfile",
        "talentProfile.basicInfo",
        "talentProfile.contact",
        "talentProfile.media"
    })
    @Query("""
        select distinct a
        from CastingApplicationEntity a
        where a.id in :ids
        """)
    List<CastingApplicationEntity> findAllForEmployerApplicantCardsByIdIn(
        @Param("ids") List<UUID> ids
    );

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update CastingApplicationEntity a
           set a.status = :status
         where a.id = :applicationId
           and a.castingRole.casting.employerProfile.id = :employerProfileId
           and a.deleted = false
        """)
    int setStatusIfOwned(
        @Param("applicationId") UUID applicationId,
        @Param("employerProfileId") UUID employerProfileId,
        @Param("status") CastingApplicationStatusOptionEntity status
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update CastingApplicationEntity a
           set a.status = :status
         where a.id in :applicationIds
           and a.castingRole.casting.employerProfile.id = :employerProfileId
           and a.deleted = false
        """)
    int setStatusIfOwnedBulk(
        @Param("applicationIds") List<UUID> applicationIds,
        @Param("employerProfileId") UUID employerProfileId,
        @Param("status") CastingApplicationStatusOptionEntity status
    );
}
