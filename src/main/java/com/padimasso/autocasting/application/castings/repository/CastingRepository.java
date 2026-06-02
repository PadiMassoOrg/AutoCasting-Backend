package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CastingRepository extends SoftDeleteRepository<CastingEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {
        "status",
        "projectType",
        "castingModality",
        "employerProfile",
        "employerProfile.basicInfo",
        "attachments",
        "roles",
        "roles.roleType",
        "roles.gender",
        "roles.professions",
        "roles.skills",
        "roles.payRateType",
        "roles.currency"
    })
    Page<CastingEntity> findAll(@Nullable Specification<CastingEntity> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
        "status",
        "projectType",
        "castingModality",
        "employerProfile",
        "employerProfile.basicInfo",
        "attachments",
        "roles",
        "roles.roleType",
        "roles.gender",
        "roles.professions",
        "roles.skills",
        "roles.payRateType",
        "roles.currency"
    })
    Optional<CastingEntity> findByDefaultCodeAndDeletedFalse(String defaultCode);

    @EntityGraph(attributePaths = {
        "status",
        "projectType",
        "castingModality",
        "employerProfile",
        "employerProfile.basicInfo",
        "attachments",
        "roles",
        "roles.roleType",
        "roles.gender",
        "roles.professions",
        "roles.skills",
        "roles.payRateType",
        "roles.currency"
    })
    Optional<CastingEntity> findByDefaultCodeAndEmployerProfile_IdAndDeletedFalse(String defaultCode, UUID employerProfileId);

    @EntityGraph(attributePaths = {
        "status",
        "projectType",
        "castingModality",
        "employerProfile",
        "employerProfile.basicInfo",
        "attachments",
        "roles",
        "roles.roleType",
        "roles.gender",
        "roles.professions",
        "roles.skills",
        "roles.payRateType",
        "roles.currency"
    })
    Optional<CastingEntity> findByIdAndEmployerProfile_IdAndDeletedFalse(UUID id, UUID employerProfileId);

    @EntityGraph(attributePaths = {
        "status",
        "projectType",
        "castingModality",
        "employerProfile",
        "employerProfile.basicInfo",
        "attachments",
        "roles",
        "roles.roleType",
        "roles.gender",
        "roles.professions",
        "roles.skills",
        "roles.payRateType",
        "roles.currency"
    })
    Optional<CastingEntity> findByIdAndDeletedFalse(UUID id);

    @Query(value = """
        select c.*
        from casting c
        join casting_status_option status on status.id = c.casting_status_option_id
        join casting_modality_option modality on modality.id = c.casting_modality_option_id
        where c.deleted = false
          and c.employer_profile_id = :employerProfileId
          and status.string_code = :draftStatusCode
          and c.title = :defaultTitle
          and c.project_type_option_id is null
          and modality.string_code = :defaultModalityCode
          and c.location_text is null
          and c.application_deadline is null
          and c.has_wardrobe_fitting = false
          and c.wardrobe_fitting_text is null
          and c.shooting_start_date is null
          and c.shooting_end_date is null
          and c.description is null
          and not exists (select 1 from casting_role r where r.casting_id = c.id and r.deleted = false)
          and not exists (select 1 from casting_attachment a where a.casting_id = c.id and a.deleted = false)
        order by c.created_at desc
        limit 1
        """, nativeQuery = true)
    Optional<CastingEntity> findLatestPristineDraftByEmployerProfileId(
        @Param("employerProfileId") UUID employerProfileId,
        @Param("draftStatusCode") String draftStatusCode,
        @Param("defaultTitle") String defaultTitle,
        @Param("defaultModalityCode") String defaultModalityCode
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update CastingEntity c
           set c.status = :status
         where c.id = :castingId
           and c.employerProfile.id = :employerProfileId
           and c.deleted = false
           and c.status.stringCode in :allowedCurrentCodes
        """)
    int setStatusIfCurrentIn(
        @Param("castingId") UUID castingId,
        @Param("employerProfileId") UUID employerProfileId,
        @Param("status") CastingStatusOptionEntity status,
        @Param("allowedCurrentCodes") List<String> allowedCurrentCodes
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update casting
           set casting_status_option_id = :closedStatusId,
               modified_at = CURRENT_TIMESTAMP
         where deleted = false
           and application_deadline is not null
           and application_deadline <= :today
           and casting_status_option_id in (:allowedStatusIds)
        """, nativeQuery = true)
    int closeExpiredCastings(
        @Param("today") LocalDate today,
        @Param("closedStatusId") UUID closedStatusId,
        @Param("allowedStatusIds") List<UUID> allowedStatusIds
    );

    long countByEmployerProfile_IdAndDeletedFalse(UUID employerProfileId);

    long countByEmployerProfile_IdAndDeletedFalseAndStatus_StringCodeIn(UUID employerProfileId, List<String> allowedStatusCodes);
}
