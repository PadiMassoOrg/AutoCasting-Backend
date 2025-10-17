package com.padimasso.autocasting.application.talent.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BasicInfoResponse(
    UUID id,
    String stageName,
    GenderOptionEntity gender,
    LocalDate birthDate,
    List<SiteMetadataObject> professions
) {
}
