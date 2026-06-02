package com.padimasso.autocasting.application.castings.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CastingUpsertRequest(
    @NotBlank(message = "casting.title_required")
    @Size(max = 255, message = "casting.title_max_length")
    String title,
    UUID projectTypeId,
    UUID castingModalityId,
    @Size(max = 255, message = "casting.location_max_length")
    String locationText,
    LocalDate applicationDeadline,
    Boolean hasWardrobeFitting,
    @Size(max = 255, message = "casting.wardrobe_fitting_text_max_length")
    String wardrobeFittingText,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    @Size(max = 3000, message = "casting.description_max_length")
    String description
) {
    @AssertTrue(message = "casting.shooting_date_range_invalid")
    public boolean isShootingRangeValid() {
        return shootingStartDate == null || shootingEndDate == null || !shootingStartDate.isAfter(shootingEndDate);
    }
}
