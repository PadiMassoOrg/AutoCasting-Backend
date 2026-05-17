package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.List;

public record PublicCastingResponse(
    PublicCastingEmployerInfoResponse employerInfo,
    String slug,
    String title,
    SiteMetadataObject projectType,
    SiteMetadataObject castingModality,
    String locationText,
    LocalDate applicationDeadline,
    String wardrobeFittingText,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    String description,
    List<PublicCastingRoleResponse> roles
) {
}
