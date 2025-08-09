package com.padimasso.autocasting.application.profile.dto.request;

import java.time.LocalDate;

public record BasicInfoPatchRequest(
    String stageName,
    String gender,
    LocalDate birthDate
) {
}
