package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRolesSectionResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface CastingRoleService {
    CastingRoleResponse createCastingRole(CastingRoleRequest request);

    List<CastingRoleEmployerCardResponse> getCastingRolesBySectionId(EmployerCastingRoleFilter filter, int page, int size);

    CastingRoleResponse updateCastingRole(UUID roleId, @Valid CastingRoleRequest request);

    void deleteCastingRole(UUID roleId);

    CastingRolesSectionResponse getBySectionId(UUID sectionId);
}
