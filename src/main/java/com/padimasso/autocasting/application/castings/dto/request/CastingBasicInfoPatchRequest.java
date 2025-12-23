package com.padimasso.autocasting.application.castings.dto.request;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.UUID;

public record CastingBasicInfoPatchRequest(
    UUID id,
    SiteMetadataObject sectionStatus,
    String title,
    UUID projectTypeId,
    String location,
    UUID castingModalityId,
    String castingModalityText,
    LocalDate applicationDeadline,
    Boolean hasWardrobeFitting,
    String wardrobeFittingText,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    JsonNullable<String> description
) {
}
