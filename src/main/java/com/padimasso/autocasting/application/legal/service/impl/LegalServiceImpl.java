package com.padimasso.autocasting.application.legal.service.impl;

import com.padimasso.autocasting.application.legal.dto.response.LegalRequirementResponse;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LegalServiceImpl implements LegalService {

    private static final List<LegalDocumentType> REQUIRED_TYPES = List.of(
        LegalDocumentType.TERMS,
        LegalDocumentType.PRIVACY
    );

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
        if (doc.getStatus() != LegalDocumentStatus.PUBLISHED || doc.getEffectiveAt().isAfter(OffsetDateTime.now())) {
            throw ApiException.badRequest("legal.document.not_current");
        }
        LegalDocument current = getCurrent(doc.getType(), doc.getLocale())
            .orElseThrow(() -> ApiException.notFound("legal.current_document.not_found", doc.getType(), doc.getLocale()));
        if (!Objects.equals(current.getId(), doc.getId())) {
            throw ApiException.badRequest("legal.document.not_current");
        }

        var latestAcceptance = accRepo.findTopByUserIdAndLegalDocumentOrderByAcceptedAtDesc(userId, doc);
        if (latestAcceptance.isPresent() && Objects.equals(latestAcceptance.get().getContentHash(), doc.getContentHash())) {
            return;
        }

        LegalAcceptance acc = new LegalAcceptance();
        acc.setUserId(userId);
        acc.setLegalDocument(doc);
        acc.setIp(ip);
        acc.setUserAgent(ua);
        acc.setContentHash(doc.getContentHash());
        accRepo.save(acc);
    }

    @Override
    public List<LegalRequirementResponse> getCurrentRequirements(UUID userId, String locale) {
        String normalizedLocale = normalizeLocale(locale);
        return REQUIRED_TYPES.stream()
            .map(type -> {
                LegalDocument current = getCurrent(type, normalizedLocale)
                    .orElseThrow(() -> ApiException.notFound("legal.current_document.not_found", type, normalizedLocale));
                boolean acceptedCurrent = isCurrentAccepted(userId, current);
                return new LegalRequirementResponse(
                    current.getId(),
                    current.getType(),
                    current.getLocale(),
                    current.getVersion(),
                    current.getTitle(),
                    current.getSlug(),
                    current.getEffectiveAt(),
                    current.getContentHash(),
                    acceptedCurrent
                );
            })
            .toList();
    }

    @Transactional
    @Override
    public void acceptCurrentRequired(UUID userId, String locale, String ip, String ua) {
        String normalizedLocale = normalizeLocale(locale);
        for (LegalDocumentType type : REQUIRED_TYPES) {
            LegalDocument current = getCurrent(type, normalizedLocale)
                .orElseThrow(() -> ApiException.notFound("legal.current_document.not_found", type, normalizedLocale));
            accept(userId, current.getId(), ip, ua);
        }
    }

    @Override
    public boolean hasAcceptedCurrentRequired(UUID userId, String locale) {
        String normalizedLocale = normalizeLocale(locale);
        for (LegalDocumentType type : REQUIRED_TYPES) {
            LegalDocument current = getCurrent(type, normalizedLocale)
                .orElseThrow(() -> ApiException.notFound("legal.current_document.not_found", type, normalizedLocale));
            if (!isCurrentAccepted(userId, current)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCurrentAccepted(UUID userId, LegalDocument current) {
        return accRepo.findTopByUserIdAndLegalDocumentOrderByAcceptedAtDesc(userId, current)
            .map(acc -> Objects.equals(acc.getContentHash(), current.getContentHash()))
            .orElse(false);
    }

    private String normalizeLocale(String locale) {
        if (locale == null || locale.isBlank()) return "es";
        String normalized = locale.trim().toLowerCase();
        if (normalized.startsWith("es")) return "es";
        return "es";
    }
}
