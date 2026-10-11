package com.padimasso.autocasting.application.events.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.UUID;

public record EventDetailsResponse(
    UUID id,
    String slug,
    String name,
    SiteMetadataObject entryAccess,
    String ticketUrl,
    LocalDate startDate,
    LocalDate endDate,
    String datesInfo,
    String location,
    String imageUrl,
    String description
) {
}
