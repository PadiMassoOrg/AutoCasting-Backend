package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.UUID;

public record CastingCardResponse(
    UUID id,
    String title,
    LocalDate creationDate,
    LocalDate applicationDeadline,
    SiteMetadataObject projectType
) {
}
