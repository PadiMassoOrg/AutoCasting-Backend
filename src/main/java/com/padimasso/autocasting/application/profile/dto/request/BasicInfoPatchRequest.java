package com.padimasso.autocasting.application.profile.dto.request;

import java.time.LocalDate;

public record BasicInfoPatchRequest(
    String name,
    String gender,
    LocalDate birthDate
) {
}
