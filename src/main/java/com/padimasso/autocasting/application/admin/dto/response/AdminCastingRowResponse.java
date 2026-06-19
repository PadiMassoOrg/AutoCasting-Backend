package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminCastingRowResponse(
    UUID id,
    String defaultCode,
    String employerCompanyName,
    String title,
    SiteMetadataObject status,
    LocalDate applicationDeadline,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy,
    boolean deleted
) {
}
