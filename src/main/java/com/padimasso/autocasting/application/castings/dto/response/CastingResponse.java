package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CastingResponse(
    UUID id,
    String defaultCode,
    SiteMetadataObject castingStatus,
    CastingEmployerInfoResponse employerInfo,
    String title,
    SiteMetadataObject projectType,
    SiteMetadataObject castingModality,
    String locationText,
    LocalDate applicationDeadline,
    Boolean hasWardrobeFitting,
    String wardrobeFittingText,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    String description,
    List<CastingRoleResponse> roles,
    boolean publishable,
    LocalDateTime modifiedAt
) {
}
