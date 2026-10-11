package com.padimasso.autocasting.application.events.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record EventCardResponse(
    UUID id,
    String slug,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    String datesInfo,
    String location,
    String imageUrl
) {
}
