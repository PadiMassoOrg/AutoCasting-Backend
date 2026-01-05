package com.padimasso.autocasting.application.castings.dto.request.section;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.UUID;

public record CastingBasicInfoPatchRequest(
    UUID id,
    SiteMetadataObject sectionStatus,
    String title,
    UUID projectTypeId,
    UUID castingModalityId,
    JsonNullable<String> castingModalityText,
    LocalDate applicationDeadline,
    Boolean hasWardrobeFitting,
    JsonNullable<String> wardrobeFittingText,
    JsonNullable<LocalDate> shootingStartDate,
    JsonNullable<LocalDate> shootingEndDate,
    JsonNullable<String> description
) {
}
