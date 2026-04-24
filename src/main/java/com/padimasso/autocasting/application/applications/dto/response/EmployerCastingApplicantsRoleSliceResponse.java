package com.padimasso.autocasting.application.applications.dto.response;

import java.util.List;

public record EmployerCastingApplicantsRoleSliceResponse(
    String roleId,
    String roleName,
    List<EmployerCastingApplicantCardResponse> items,
    boolean hasNext,
    int page,
    int size
) {
}
