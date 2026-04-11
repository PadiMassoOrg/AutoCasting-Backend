package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TalentProfileRepository extends SoftDeleteRepository<TalentProfileEntity, UUID>, JpaSpecificationExecutor<TalentProfileEntity> {
    Optional<TalentProfileEntity> findByUserId(UUID id);

    Optional<TalentProfileEntity> findByDefaultSlugOrPremiumSlug(String slug, String slug1);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update TalentProfileEntity t
           set t.modifiedAt = CURRENT_TIMESTAMP
         where t.id = :profileId
           and t.deleted = false
        """)
    void touchModifiedAt(@Param("profileId") UUID profileId);

    @Query("""
        select t.modifiedAt
        from TalentProfileEntity t
        where t.id = :profileId
          and t.deleted = false
        """)
    LocalDateTime findModifiedAtById(@Param("profileId") UUID profileId);

    @Override
    Page<TalentProfileEntity> findAll(@Nullable Specification<TalentProfileEntity> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
        "basicInfo",
        "basicInfo.professions",
        "contact",
        "media",
        "plan"
    })
    @Query("""
        select distinct t
        from TalentProfileEntity t
        where t.id in :ids
        """)
    List<TalentProfileEntity> findAllForTalentCardsByIdIn(
        @Param("ids") List<UUID> ids
    );
}

