package com.padimasso.autocasting.application.castings.dto.response.card;

import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CastingRoleEmployerCardResponse(
    UUID id,
    UUID castingId,
    String roleName,
    SiteMetadataObject gender,
    Short ageMin,
    Short ageMax,
    List<SiteMetadataObject> professions,
    SiteMetadataObject roleType,
    List<SiteMetadataObject> skills,
    CastingRoleRemunerationResponse remuneration,
    LocalDateTime modifiedAt
) {
}
