package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
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

    @Query("""
        select c
        from CastingEntity c
        left join fetch c.basicInfo bi
        left join fetch bi.projectType pto
        order by c.createdAt desc
        """)
    List<CastingEntity> findAllCards();

}
