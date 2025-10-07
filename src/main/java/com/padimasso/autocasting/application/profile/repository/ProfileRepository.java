package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends SoftDeleteRepository<ProfileEntity, UUID>, JpaSpecificationExecutor<ProfileEntity> {
    Optional<ProfileEntity> findByUserId(UUID id);

    Optional<ProfileEntity> findByDefaultSlugOrPremiumSlug(String slug, String slug1);

    @Override
    @EntityGraph(attributePaths = {
        "basicInfo", "basicInfo.professions",
        "contact",
        "media",
        "plan"
    })
    Page<ProfileEntity> findAll(@Nullable Specification<ProfileEntity> spec, Pageable pageable);
}


