package com.padimasso.autocasting.application.applications.dto;

import com.padimasso.autocasting.application.applications.repository.order.EmployerCastingApplicantsOrderBy;

import java.util.List;
import java.util.UUID;

public record EmployerCastingApplicantsFilter(
    UUID employerProfileId,
    String castingSlug,
    UUID castingRoleId,
    String search,
    List<String> applicationStatusIdTokens,
    List<UUID> professionIds,
    EmployerCastingApplicantsOrderBy orderBy
) {
}
