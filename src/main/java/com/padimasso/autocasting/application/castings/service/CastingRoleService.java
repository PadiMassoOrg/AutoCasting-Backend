package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;

public interface CastingRoleService {
    CastingRoleResponse createCastingRole(CastingRoleRequest request);
}
