package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRequirementsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRequirementBulkRequest;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRequirementCardResponse;

import java.util.List;

public interface CastingRequirementService {

    List<CastingRequirementCardResponse> getRequirementsBySectionId(EmployerCastingRequirementsFilter filter, int page, int size);

    List<CastingRequirementCardResponse> createRequirementsBulk(CastingRequirementBulkRequest request);

}
