package com.padimasso.autocasting.application.profile.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BasicInfoResponse(
    UUID id,
    String stageName,
    String gender,
    LocalDate birthDate,
    List<SiteMetadataObject> professions
) {
}
