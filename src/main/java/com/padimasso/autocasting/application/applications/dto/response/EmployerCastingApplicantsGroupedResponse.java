package com.padimasso.autocasting.application.applications.dto.response;

import java.util.List;

public record EmployerCastingApplicantsGroupedResponse(
    String castingSlug,
    List<EmployerCastingApplicantsRoleSliceResponse> roles
) {
}
