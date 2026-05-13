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
