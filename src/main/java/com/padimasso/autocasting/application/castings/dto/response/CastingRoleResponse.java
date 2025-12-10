package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.response.CharacteristicsResponse;

import java.util.Set;
import java.util.UUID;

public record CastingRoleResponse(
    UUID id,
    boolean isComplete,
    String name,
    SiteMetadataObject roleType,
    SiteMetadataObject gender,
    Short ageMin,
    Short ageMax,
    String description,
    CharacteristicsResponse characteristics,
    Set<SiteMetadataObject> skills
) {
}
