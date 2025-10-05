package com.padimasso.autocasting.application.legal.service;

import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.model.LegalDocumentType;

import java.util.Optional;
import java.util.UUID;

public interface LegalService {
    Optional<LegalDocument> getCurrent(LegalDocumentType type, String locale);

    void accept(UUID userId, UUID documentId, String ip, String ua);

}
