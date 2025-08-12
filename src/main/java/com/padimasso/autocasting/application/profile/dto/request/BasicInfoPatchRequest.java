package com.padimasso.autocasting.application.profile.dto.request;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BasicInfoPatchRequest(
    String stageName,
    String gender,
    LocalDate birthDate,
    Set<UUID> professionIds
) {
}
