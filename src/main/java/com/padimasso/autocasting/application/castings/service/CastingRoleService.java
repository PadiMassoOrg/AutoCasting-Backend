package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;

import java.util.List;
import java.util.UUID;

public interface CastingRoleService {
    CastingRoleResponse createCastingRole(CastingRoleRequest request);

    List<CastingRoleEmployerCardResponse> getCastingRolesBySectionId(UUID sectionId);
}
