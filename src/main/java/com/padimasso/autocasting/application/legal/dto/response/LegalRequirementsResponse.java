package com.padimasso.autocasting.application.legal.dto.response;

import java.util.List;

public record LegalRequirementsResponse(
    String locale,
    boolean acceptedCurrent,
    List<LegalRequirementResponse> requiredDocuments
) {
}

