package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.response.CharacteristicsResponse;

import java.util.List;
import java.util.UUID;

public record CastingRoleResponse(
    UUID id,
    UUID sectionId,
    String roleName,
    SiteMetadataObject roleType,
    SiteMetadataObject gender,
    Short ageMin,
    Short ageMax,
    String description,
    List<SiteMetadataObject> professions,
    CharacteristicsResponse characteristics,
    List<SiteMetadataObject> skills,
    CastingRoleRemunerationResponse remuneration
) {
}
