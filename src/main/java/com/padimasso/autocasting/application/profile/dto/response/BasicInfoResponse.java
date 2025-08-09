package com.padimasso.autocasting.application.profile.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record BasicInfoResponse(
    UUID id,
    String stageName,
    String gender,
    LocalDate birthDate
) {
}
