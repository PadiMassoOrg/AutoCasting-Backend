package com.padimasso.autocasting.application.legal.service.impl;

import com.padimasso.autocasting.application.legal.model.LegalAcceptance;
import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.model.LegalDocumentStatus;
import com.padimasso.autocasting.application.legal.model.LegalDocumentType;
import com.padimasso.autocasting.application.legal.repository.LegalAcceptanceRepository;
import com.padimasso.autocasting.application.legal.repository.LegalDocumentRepository;
import com.padimasso.autocasting.application.legal.service.LegalService;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LegalServiceImpl implements LegalService {

    private final LegalDocumentRepository docRepo;
    private final LegalAcceptanceRepository accRepo;

    @Override
    public Optional<LegalDocument> getCurrent(LegalDocumentType type, String locale) {
        return docRepo.findFirstByTypeAndLocaleAndStatusAndEffectiveAtLessThanEqualOrderByEffectiveAtDesc(
            type, locale, LegalDocumentStatus.PUBLISHED, OffsetDateTime.now());
    }

    @Transactional
    @Override
    public void accept(UUID userId, UUID documentId, String ip, String ua) {
        LegalDocument doc = docRepo.findById(documentId)
            .orElseThrow(() -> ApiException.notFound("legal.document.not_found"));
        // (Opcional) asegurate que es el current si querés forzarlo:
        // getCurrent(doc.getType(), doc.getLocale()).filter(d -> Objects.equals(d.getId(), doc.getId())).orElseThrow();

        LegalAcceptance acc = new LegalAcceptance();
        acc.setUserId(userId);
        acc.setLegalDocument(doc);
        acc.setIp(ip);
        acc.setUserAgent(ua);
        acc.setContentHash(doc.getContentHash());
        accRepo.save(acc);
    }
}
