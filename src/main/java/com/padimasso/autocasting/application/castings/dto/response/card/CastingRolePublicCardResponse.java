package com.padimasso.autocasting.application.castings.dto.response.card;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.UUID;

public record CastingRolePublicCardResponse(
    // Casting Role
    UUID id,
    String name,
    // Casting
    String castingTitle,
    // Employer Basic Info
    String employerImageUrl,
    // Casting Basic Info
    SiteMetadataObject projectType,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    // Casting Role
    SiteMetadataObject roleType,
    SiteMetadataObject gender,
    Short ageMin,
    Short ageMax,
    // Casting
    String defaultCode
) {
}
