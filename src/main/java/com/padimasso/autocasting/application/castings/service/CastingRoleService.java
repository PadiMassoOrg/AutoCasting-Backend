package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface CastingRoleService {
    CastingRoleResponse createCastingRole(CastingRoleRequest request);

    List<CastingRoleEmployerCardResponse> getCastingRolesByCastingId(EmployerCastingRoleFilter filter, int page, int size);

    CastingRoleResponse updateCastingRole(UUID roleId, @Valid CastingRoleRequest request);

    LastModifiedResponse deleteCastingRole(UUID roleId);

    CastingRoleResponse getById(UUID roleId);

    CastingRoleResponse duplicateCastingRole(UUID roleId, String roleName);
}
