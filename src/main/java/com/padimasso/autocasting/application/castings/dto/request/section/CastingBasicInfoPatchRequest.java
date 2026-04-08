package com.padimasso.autocasting.application.castings.dto.request.section;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.UUID;

public record CastingBasicInfoPatchRequest(
    @NotNull(message = "casting.basic_info_id_required")
    UUID id,
    SiteMetadataObject sectionStatus,
    @Size(max = 255, message = "casting.title_max_length")
    String title,
    UUID projectTypeId,
    UUID castingModalityId,
    @Size(max = 255, message = "casting.casting_modality_text_max_length")
    JsonNullable<String> castingModalityText,
    LocalDate applicationDeadline,
    JsonNullable<Boolean> hasWardrobeFitting,
    @Size(max = 255, message = "casting.wardrobe_fitting_text_max_length")
    JsonNullable<String> wardrobeFittingText,
    JsonNullable<LocalDate> shootingStartDate,
    JsonNullable<LocalDate> shootingEndDate,
    @Size(max = 255, message = "casting.description_max_length")
    JsonNullable<String> description
) {
}
