package com.padimasso.autocasting.application.castings.dto;

import com.padimasso.autocasting.application.castings.repository.order.EmployerCastingsOrderBy;

import java.util.List;
import java.util.UUID;

public record EmployerCastingsFilter(
    UUID employerProfileId,
    List<String> projectTypeIdTokens,
    List<String> statusIdTokens,
    EmployerCastingsOrderBy orderBy
) {
}
