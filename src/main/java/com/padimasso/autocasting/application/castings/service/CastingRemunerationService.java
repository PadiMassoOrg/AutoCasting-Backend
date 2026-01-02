package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.request.CastingRemunerationPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationResponse;

public interface CastingRemunerationService {
    CastingRoleRemunerationResponse patchRoleRemuneration(CastingRemunerationPatchRequest request);
}
