package com.padimasso.autocasting.application.legal.service;

import com.padimasso.autocasting.application.legal.model.LegalDocument;

import java.util.Optional;

public interface LegalPdfUrlService {
    Optional<String> buildSignedDownloadUrl(LegalDocument document);

    String buildObjectKey(LegalDocument document);
}

