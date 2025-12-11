package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CastingRepository extends SoftDeleteRepository<CastingEntity, UUID> {
    Optional<CastingEntity> findByDefaultCode(String slug);

    @Query("""
        select c
        from CastingEntity c
        left join fetch c.basicInfo bi
        left join fetch bi.projectType pto
        where c.employerProfile.id = :employerProfileId
        order by c.createdAt desc
        """)
    List<CastingEntity> findAllByEmployerProfileId(UUID employerProfileId);

    @Override
    @EntityGraph(attributePaths = {
        "rolesSection",
        "rolesSection.casting",
        "rolesSection.casting.basicInfo",
        "rolesSection.casting.basicInfo.projectType",
        "rolesSection.casting.basicInfo.castingModality",
        "rolesSection.casting.employerProfile",
        "rolesSection.casting.employerProfile.basicInfo",
        "professions",
        "roleType",
        "gender",
        "skills"
    })
    Page<CastingEntity> findAll(@Nullable Specification<CastingEntity> spec, Pageable pageable);

}
