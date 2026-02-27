package com.padimasso.autocasting.application.applications.repository;

import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CastingApplicationRepository extends SoftDeleteRepository<CastingApplicationEntity, UUID> {
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
}
