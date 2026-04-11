package com.padimasso.autocasting.application.castings.dto.response.section;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CastingBasicInfoResponse(
    UUID id,
    SiteMetadataObject sectionStatus,
    String title,
    SiteMetadataObject projectType,
    SiteMetadataObject castingModality,
    String castingModalityText,
    LocalDate applicationDeadline,
    Boolean hasWardrobeFitting,
    String wardrobeFittingText,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    String description,
    LocalDateTime modifiedAt
) {
}
