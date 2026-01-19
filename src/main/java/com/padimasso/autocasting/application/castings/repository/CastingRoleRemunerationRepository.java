package com.padimasso.autocasting.application.castings.repository;

import com.padimasso.autocasting.application.castings.model.CastingRoleRemunerationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CastingRoleRemunerationRepository extends SoftDeleteRepository<CastingRoleRemunerationEntity, UUID> {

    @Query("""
            select rr
            from CastingRoleRemunerationEntity rr
            join fetch rr.castingRole cr
            join cr.rolesSection rs
            join rs.casting c
            join c.remuneration sec
            left join fetch rr.payRateType
            left join fetch rr.currency
            where sec.id = :sectionId
            order by cr.createdAt desc
        """)
    List<CastingRoleRemunerationEntity> findAllByRemunerationSectionId(@Param("sectionId") UUID sectionId);
}
