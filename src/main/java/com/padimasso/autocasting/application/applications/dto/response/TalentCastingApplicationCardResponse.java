package com.padimasso.autocasting.application.applications.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.List;

public record TalentCastingApplicationCardResponse(
    String roleName,
    String castingName,
    SiteMetadataObject castingProjectType,
    SiteMetadataObject castingModality,
    SiteMetadataObject castingStatus,
    String castingRoleId,
    String castingSlug,
    String companyName,
    String employerImageUrl,
    String castingModalityText,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    SiteMetadataObject gender,
    List<SiteMetadataObject> professions,
    SiteMetadataObject roleType
) {
}
