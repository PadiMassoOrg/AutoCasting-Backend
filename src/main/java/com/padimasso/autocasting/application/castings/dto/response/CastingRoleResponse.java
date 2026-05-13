package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CastingRoleResponse(
    UUID id,
    UUID castingId,
    String roleName,
    SiteMetadataObject roleType,
    SiteMetadataObject gender,
    Short ageMin,
    Short ageMax,
    String description,
    List<SiteMetadataObject> professions,
    List<SiteMetadataObject> skills,
    CastingRoleRemunerationResponse remuneration,
    SiteMetadataObject ethnicity,
    Boolean tattoo,
    Boolean passport,
    Boolean drivingLicense,
    boolean requiresAudio,
    boolean requiresVideo,
    String requirementDescription,
    LocalDateTime modifiedAt
) {
}
