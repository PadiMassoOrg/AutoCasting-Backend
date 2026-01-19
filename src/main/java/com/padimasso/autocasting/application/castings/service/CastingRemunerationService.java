package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRemunerationPatchRequest;
import com.padimasso.autocasting.application.castings.dto.request.section.CastingRemunerationsSectionPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRemunerationsSectionResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface CastingRemunerationService {
    CastingRoleRemunerationResponse patchRoleRemuneration(CastingRoleRemunerationPatchRequest request);

    CastingRemunerationsSectionResponse getBySectionId(UUID sectionId);

    CastingRemunerationsSectionResponse patchSectionRemuneration(@Valid CastingRemunerationsSectionPatchRequest request);
}
