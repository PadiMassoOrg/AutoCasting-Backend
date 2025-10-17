package com.padimasso.autocasting.application.legal.repository;

import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.model.LegalDocumentStatus;
import com.padimasso.autocasting.application.legal.model.LegalDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LegalDocumentRepository extends JpaRepository<LegalDocument, UUID> {

    Optional<LegalDocument> findFirstByTypeAndLocaleAndStatusAndEffectiveAtLessThanEqualOrderByEffectiveAtDesc(
        LegalDocumentType type, String locale, LegalDocumentStatus status, OffsetDateTime now);

    List<LegalDocument> findByTypeAndLocaleOrderByEffectiveAtDesc(LegalDocumentType type, String locale);
}
