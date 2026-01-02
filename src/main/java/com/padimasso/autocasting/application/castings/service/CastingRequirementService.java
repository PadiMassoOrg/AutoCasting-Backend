package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRequirementsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRequirementBulkRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRequirementResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRequirementCardResponse;

import java.util.List;
import java.util.UUID;

public interface CastingRequirementService {

    List<CastingRequirementCardResponse> getRequirementsBySectionId(EmployerCastingRequirementsFilter filter, int page, int size);

    List<CastingRequirementCardResponse> createRequirementsBulk(CastingRequirementBulkRequest request);

    CastingRequirementResponse updateCastingRequirement(UUID requirementId, CastingRequirementBulkRequest request);

    void deleteCastingRequirement(UUID requirementId);
}
