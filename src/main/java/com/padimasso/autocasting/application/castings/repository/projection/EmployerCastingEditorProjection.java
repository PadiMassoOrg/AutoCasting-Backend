package com.padimasso.autocasting.application.castings.repository.projection;

import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;

import java.util.UUID;

public interface EmployerCastingEditorProjection {
    UUID getId();

    String getDefaultCode();

    CastingStatusOptionEntity getStatus();

    UUID getBasicInfoSectionId();

    UUID getRolesSectionId();

    UUID getRequirementsSectionId();

    UUID getRemunerationSectionId();

    CastingSectionStatusOptionEntity getBasicInfoSectionStatus();

    CastingSectionStatusOptionEntity getRolesSectionStatus();

    CastingSectionStatusOptionEntity getRequirementsSectionStatus();

    CastingSectionStatusOptionEntity getRemunerationSectionStatus();
}
