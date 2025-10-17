package com.padimasso.autocasting.application.legal.dto.response;

import com.padimasso.autocasting.application.legal.model.LegalDocumentType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LegalDocumentResponse(
    UUID id,
    LegalDocumentType type,
    String locale,
    String version,
    String title,
    String slug,
    OffsetDateTime effectiveAt,
    String contentHtml,
    String contentHash
) {
}
