package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.UUID;

public record CastingBasicInfoResponse(
    UUID id,
    SiteMetadataObject sectionStatus,
    String title,
    SiteMetadataObject projectType,
    String location,
    SiteMetadataObject castingModality,
    String castingModalityText,
    LocalDate applicationDeadline,
    boolean hasWardrobeFitting,
    String wardrobeFittingText,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    String description
) {
}
