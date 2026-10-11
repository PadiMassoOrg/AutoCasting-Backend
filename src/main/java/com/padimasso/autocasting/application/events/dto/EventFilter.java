package com.padimasso.autocasting.application.events.dto;

import java.time.LocalDate;

public record EventFilter(
    LocalDate dateFrom,
    LocalDate dateTo
) {
}
