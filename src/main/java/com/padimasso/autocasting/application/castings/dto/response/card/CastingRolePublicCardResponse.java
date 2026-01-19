package com.padimasso.autocasting.application.castings.dto.response.card;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CastingRolePublicCardResponse(
    // Casting Role
    UUID id,
    String name,
    // Employer Basic Info
    String employerImageUrl,
    String employerCompanyName,
    // Casting Basic Info
    SiteMetadataObject projectType,
    SiteMetadataObject castingModality,
    String location,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    // Casting Role
    List<SiteMetadataObject> professions,
    SiteMetadataObject roleType,
    SiteMetadataObject gender,
    Short ageMin,
    Short ageMax,
    // Casting
    String defaultCode
) {
}
