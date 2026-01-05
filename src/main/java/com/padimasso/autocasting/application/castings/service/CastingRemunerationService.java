package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.request.CastingRemunerationPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRemunerationsSectionResponse;

import java.util.UUID;

public interface CastingRemunerationService {
    CastingRoleRemunerationResponse patchRoleRemuneration(CastingRemunerationPatchRequest request);

    CastingRemunerationsSectionResponse getBySectionId(UUID sectionId);
}
