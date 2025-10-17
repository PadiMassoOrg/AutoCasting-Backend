package com.padimasso.autocasting.application.legal.repository;

import com.padimasso.autocasting.application.legal.model.LegalAcceptance;
import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.model.LegalDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LegalAcceptanceRepository extends JpaRepository<LegalAcceptance, UUID> {

    @Query("""
    select la from LegalAcceptance la
    where la.userId = :userId
      and la.legalDocument.type = :type
    order by la.acceptedAt desc
  """)
    List<LegalAcceptance> findAllByUserAndType(UUID userId, LegalDocumentType type);

    Optional<LegalAcceptance> findTopByUserIdAndLegalDocumentOrderByAcceptedAtDesc(UUID userId, LegalDocument doc);
}
