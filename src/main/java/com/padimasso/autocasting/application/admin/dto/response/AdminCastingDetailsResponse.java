package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminCastingDetailsResponse(
    UUID id,
    String defaultCode,
    SiteMetadataObject castingStatus,
    String employerCompanyName,
    String title,
    SiteMetadataObject projectType,
    SiteMetadataObject castingModality,
    String locationText,
    LocalDate applicationDeadline,
    Boolean hasWardrobeFitting,
    String wardrobeFittingText,
    LocalDate shootingStartDate,
    LocalDate shootingEndDate,
    String description,
    List<AdminCastingRoleRowResponse> roles,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy,
    boolean deleted
) {
}
