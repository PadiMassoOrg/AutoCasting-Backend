package com.padimasso.autocasting.application.legal.dto.response;

import com.padimasso.autocasting.application.legal.model.LegalDocumentType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LegalRequirementResponse(
    UUID documentId,
    LegalDocumentType type,
    String locale,
    String version,
    String title,
    String slug,
    OffsetDateTime effectiveAt,
    String contentHash,
    boolean acceptedCurrent
) {
}

