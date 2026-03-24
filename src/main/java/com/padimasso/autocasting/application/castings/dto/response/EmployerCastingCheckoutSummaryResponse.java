package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EmployerCastingCheckoutSummaryResponse(
    UUID id,
    String defaultCode,
    String castingTitle,
    SiteMetadataObject projectType,
    SiteMetadataObject castingModality,
    LocalDate applicationDeadline,
    List<EmployerCastingCheckoutRoleResponse> roles
) {
}
