package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TalentProfileRepository extends SoftDeleteRepository<TalentProfileEntity, UUID>, JpaSpecificationExecutor<TalentProfileEntity> {
    Optional<TalentProfileEntity> findByUserId(UUID id);

    Optional<TalentProfileEntity> findByDefaultSlugOrPremiumSlug(String slug, String slug1);

    @Override
    @EntityGraph(attributePaths = {
        "basicInfo", "basicInfo.professions",
        "contact",
        "media",
        "plan"
    })
    Page<TalentProfileEntity> findAll(@Nullable Specification<TalentProfileEntity> spec, Pageable pageable);
}


