package com.padimasso.autocasting.application.castings.dto.response.card;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.UUID;

public record CastingCardResponse(
    UUID id,
    String title,
    String defaultCode,
    LocalDate creationDate,
    LocalDate applicationDeadline,
    SiteMetadataObject projectType,
    SiteMetadataObject status
) {
}
