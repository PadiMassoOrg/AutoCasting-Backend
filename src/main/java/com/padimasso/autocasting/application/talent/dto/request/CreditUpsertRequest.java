package com.padimasso.autocasting.application.talent.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreditUpsertRequest(
    UUID id,
    @Valid
    @NotNull(message = "validation.required")
    CreditRequest credit
) {
}
