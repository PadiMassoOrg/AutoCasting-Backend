package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.repository.projection.CastingCardStatusGateProjection;
import com.padimasso.autocasting.application.castings.repository.projection.CastingPublishGateProjection;
import com.padimasso.autocasting.application.castings.repository.projection.EmployerCastingEditorProjection;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
            rem.id as remunerationSectionId,
            biSS as basicInfoSectionStatus,
            rsSS as rolesSectionStatus,
            reqSS as requirementsSectionStatus,
            remSS as remunerationSectionStatus
        from CastingEntity c
            left join c.status s
            left join c.basicInfo bi
            left join bi.sectionStatus biSS
            left join c.roles rs
            left join rs.sectionStatus rsSS
            left join c.requirements req
            left join req.sectionStatus reqSS
            left join c.remuneration rem
            left join rem.sectionStatus remSS
        where c.defaultCode = :slug
          and c.deleted = false
        """)
    Optional<EmployerCastingEditorProjection> findCastingEditorProjectionBySlug(@Param("slug") String slug);

    @EntityGraph(attributePaths = {
        "status",
        "employerProfile",
        "employerProfile.basicInfo",
        "employerProfile.basicInfo.companyType",

        "basicInfo",
        "basicInfo.sectionStatus",
        "basicInfo.projectType",
        "basicInfo.castingModality",

        "roles",
        "roles.sectionStatus",
        "roles.roles",
        "roles.roles.roleType",
        "roles.roles.gender",
        "roles.roles.professions",
        "roles.roles.skills",
        "roles.roles.remuneration",
        "roles.roles.remuneration.payRateType",
        "roles.roles.remuneration.currency",

        "requirements",
        "requirements.sectionStatus",
        "requirements.requirements",
        "requirements.requirements.castingRole",

        "remuneration",
        "remuneration.sectionStatus",
        "remuneration.compensationType"
    })
    @Query("""
          select c
          from CastingEntity c
          where c.defaultCode = :slug
            and c.deleted = false
            and c.employerProfile.id = :employerProfileId
        """)
    Optional<CastingEntity> findEmployerDetailsByDefaultCodeAndEmployerProfileId(
        @Param("slug") String slug,
        @Param("employerProfileId") UUID employerProfileId
    );

    @Query("""
            select distinct c
            from CastingEntity c
                join fetch c.roles rs
                join fetch rs.roles r
                left join fetch r.roleType
                left join fetch r.gender
                left join fetch r.professions
                left join fetch r.skills
                left join fetch r.remuneration rem
                left join fetch rem.payRateType
                left join fetch rem.currency
                left join fetch c.status
                left join fetch c.employerProfile ep
                left join fetch ep.basicInfo
                left join fetch c.basicInfo bi
                left join fetch bi.sectionStatus
                left join fetch c.requirements req
                left join fetch req.sectionStatus
                left join fetch c.remuneration ren
                left join fetch ren.sectionStatus
            where c.defaultCode = :slug
              and c.deleted = false
              and c.status.stringCode in :allowedStatusCodes
              and r.id = :roleId
        """)
    Optional<CastingEntity> findPublicDetailsBySlugAndRoleId(
        @Param("slug") String slug,
        @Param("roleId") UUID roleId,
        @Param("allowedStatusCodes") List<String> allowedStatusCodes
    );

    // Casting Statuses
    @Query("""
        select
            c.id as id,
            s.stringCode as castingStatusCode,
            biSS.stringCode as basicInfoStatusCode,
            rsSS.stringCode as rolesStatusCode,
            reqSS.stringCode as requirementsStatusCode,
            remSS.stringCode as remunerationStatusCode,
            bi.applicationDeadline as applicationDeadline
        from CastingEntity c
            join c.status s
            left join c.basicInfo bi
            left join bi.sectionStatus biSS
            left join c.roles rs
            left join rs.sectionStatus rsSS
            left join c.requirements req
            left join req.sectionStatus reqSS
            left join c.remuneration rem
            left join rem.sectionStatus remSS
        where c.employerProfile.id = :employerProfileId
          and c.deleted = false
          and c.id in :castingIds
        """)
    List<CastingCardStatusGateProjection> findCardsGateForEmployer(
        @Param("employerProfileId") UUID employerProfileId,
        @Param("castingIds") List<UUID> castingIds
    );


    @Query("""
        select
            s.stringCode as castingStatusCode,
            biSS.stringCode as basicInfoStatusCode,
            rsSS.stringCode as rolesStatusCode,
            reqSS.stringCode as requirementsStatusCode,
            remSS.stringCode as remunerationStatusCode,
            bi.applicationDeadline as applicationDeadline
        from CastingEntity c
            join c.status s
            left join c.basicInfo bi
            left join bi.sectionStatus biSS
            left join c.roles rs
            left join rs.sectionStatus rsSS
            left join c.requirements req
            left join req.sectionStatus reqSS
            left join c.remuneration rem
            left join rem.sectionStatus remSS
        where c.id = :castingId
          and c.employerProfile.id = :employerProfileId
          and c.deleted = false
        """)
    Optional<CastingPublishGateProjection> findPublishGateForEmployer(
        @Param("castingId") UUID castingId,
        @Param("employerProfileId") UUID employerProfileId
    );

    @Modifying
    @Query("""
        update CastingEntity c
           set c.status = :publishedStatus
         where c.id = :castingId
           and c.employerProfile.id = :employerProfileId
           and c.deleted = false
           and (c.status.stringCode = :draftCode or c.status.stringCode = :pausedCode)
        """)
    int publishIfAllowed(
        @Param("castingId") UUID castingId,
        @Param("employerProfileId") UUID employerProfileId,
        @Param("publishedStatus") CastingStatusOptionEntity publishedStatus,
        @Param("draftCode") String draftCode,
        @Param("pausedCode") String pausedCode
    );

    @Override
    @EntityGraph(attributePaths = {
        "basicInfo",
        "basicInfo.projectType",
        "basicInfo.castingModality",
        "employerProfile",
        "employerProfile.basicInfo",
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

    // Internal: FORCE DRAFT is needed
    @Query("""
            select
                s.stringCode as castingStatusCode,
                biSS.stringCode as basicInfoStatusCode,
                rsSS.stringCode as rolesStatusCode,
                reqSS.stringCode as requirementsStatusCode,
                remSS.stringCode as remunerationStatusCode,
                bi.applicationDeadline as applicationDeadline
            from CastingEntity c
                join c.status s
                left join c.basicInfo bi
                left join bi.sectionStatus biSS
                left join c.roles rs
                left join rs.sectionStatus rsSS
                left join c.requirements req
                left join req.sectionStatus reqSS
                left join c.remuneration rem
                left join rem.sectionStatus remSS
            where c.id = :castingId
              and c.deleted = false
        """)
    Optional<CastingPublishGateProjection> findPublishGateByCastingId(@Param("castingId") UUID castingId);

    @Modifying
    @Query("""
        update CastingEntity c
           set c.status = :draftStatus
         where c.id = :castingId
           and c.deleted = false
           and c.status.stringCode = :publishedCode
        """)
    void forceDraftIfPublished(
        @Param("castingId") UUID castingId,
        @Param("draftStatus") CastingStatusOptionEntity draftStatus,
        @Param("publishedCode") String publishedCode
    );

    @Modifying
    @Query("""
        update CastingEntity c
           set c.status = :nextStatus
         where c.id = :castingId
           and c.employerProfile.id = :employerProfileId
           and c.deleted = false
           and c.status.stringCode in :allowedCurrentCodes
        """)
    int setStatusIfCurrentIn(
        @Param("castingId") UUID castingId,
        @Param("employerProfileId") UUID employerProfileId,
        @Param("nextStatus") CastingStatusOptionEntity nextStatus,
        @Param("allowedCurrentCodes") List<String> allowedCurrentCodes
    );

    @Query("""
          select count(c)
          from CastingEntity c
          where c.deleted = false
            and c.employerProfile.id = :employerProfileId
        """)
    long countByEmployerProfileIdAndDeletedFalse(@Param("employerProfileId") UUID employerProfileId);

    @Query("""
            select count(c)
            from CastingEntity c
            where c.deleted = false
              and c.employerProfile.id = :employerProfileId
              and c.status.stringCode in :allowedStatusCodes
        """)
    long countPublicCastingsByEmployerProfileId(
        @Param("employerProfileId") UUID employerProfileId,
        @Param("allowedStatusCodes") List<String> allowedStatusCodes
    );

}
