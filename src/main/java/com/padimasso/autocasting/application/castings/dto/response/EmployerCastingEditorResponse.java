package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EmployerCastingEditorResponse(
    UUID id,
    String defaultCode,
    SiteMetadataObject castingStatus,
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
    List<CastingRoleEmployerCardResponse> roles,
    boolean publishable,
    LocalDateTime modifiedAt
) {
}
