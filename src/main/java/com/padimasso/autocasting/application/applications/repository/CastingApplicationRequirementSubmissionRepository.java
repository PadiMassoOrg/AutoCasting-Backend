package com.padimasso.autocasting.application.applications.repository;

import com.padimasso.autocasting.application.applications.model.CastingApplicationRequirementSubmissionEntity;
import com.padimasso.autocasting.application.applications.repository.projection.ApplicationRequirementSubmissionProjection;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CastingApplicationRequirementSubmissionRepository extends SoftDeleteRepository<CastingApplicationRequirementSubmissionEntity, UUID> {

    @Query("""
        select
            s.application.id as applicationId,
            cr.id as castingRequirementId,
            cr.requiresAudio as requiresAudio,
            cr.requiresVideo as requiresVideo,
            s.audioUrl as audioUrl,
            s.videoUrl as videoUrl,
            s.notes as notes
        from CastingApplicationRequirementSubmissionEntity s
            join s.castingRequirement cr
        where s.deleted = false
          and s.application.id in :applicationIds
        """)
    List<ApplicationRequirementSubmissionProjection> findAllByApplicationIds(@Param("applicationIds") List<UUID> applicationIds);
    
}
