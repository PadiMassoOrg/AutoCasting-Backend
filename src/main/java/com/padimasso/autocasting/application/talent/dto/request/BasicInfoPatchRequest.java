package com.padimasso.autocasting.application.talent.dto.request;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BasicInfoPatchRequest(
    String stageName,
    UUID genderId,
    LocalDate birthDate,
    Set<UUID> professionIds
) {
}
