package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;

import java.util.List;

public interface CastingRoleService {
    CastingRoleResponse createCastingRole(CastingRoleRequest request);

    List<CastingRoleEmployerCardResponse> getCastingRolesBySectionId(EmployerCastingRoleFilter filter, int page, int size);
}
