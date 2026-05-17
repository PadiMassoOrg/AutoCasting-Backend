package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
import java.util.UUID;

public record PublicCastingRoleResponse(
    UUID id,
    String roleName,
    SiteMetadataObject roleType,
    SiteMetadataObject gender,
    Short ageMin,
    Short ageMax,
    String description,
    List<SiteMetadataObject> professions,
    List<SiteMetadataObject> skills,
    PublicCastingRoleRemunerationResponse remuneration,
    SiteMetadataObject ethnicity,
    Boolean tattoo,
    Boolean passport,
    Boolean drivingLicense,
    boolean requiresAudio,
    boolean requiresVideo,
    String requirementDescription
) {
}
