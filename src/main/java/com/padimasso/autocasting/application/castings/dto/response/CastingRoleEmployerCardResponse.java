package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
import java.util.UUID;

public record CastingRoleEmployerCardResponse(
    // Casting Role
    UUID id,
    String roleName,
    // Casting Role
    SiteMetadataObject gender,
    Short ageMin,
    Short ageMax,
    List<SiteMetadataObject> professions,
    SiteMetadataObject roleType,
    List<SiteMetadataObject> skills
) {
}
