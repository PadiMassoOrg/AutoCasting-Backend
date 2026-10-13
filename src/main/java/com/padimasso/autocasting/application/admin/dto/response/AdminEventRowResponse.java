package com.padimasso.autocasting.application.admin.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record AdminEventRowResponse(
    UUID id,
    String slug,
    String name,
    LocalDate startDate,
    LocalDate endDate
) {
}
