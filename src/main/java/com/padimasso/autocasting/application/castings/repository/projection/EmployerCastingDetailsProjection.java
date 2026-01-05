package com.padimasso.autocasting.application.castings.repository.projection;

import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;

import java.util.UUID;

public interface EmployerCastingDetailsProjection {
    UUID getId();

    String getDefaultCode();

    CastingStatusOptionEntity getStatus();

    UUID getBasicInfoSectionId();

    UUID getRolesSectionId();

    UUID getRequirementsSectionId();

    UUID getRemunerationSectionId();
}
