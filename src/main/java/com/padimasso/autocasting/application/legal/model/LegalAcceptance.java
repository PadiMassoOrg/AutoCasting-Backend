package com.padimasso.autocasting.application.legal.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "legal_acceptances",
    indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "legal_document_id")
    })
@Getter
@Setter
@NoArgsConstructor
public class LegalAcceptance extends AuditableEntity {

    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "legal_document_id")
    private LegalDocument legalDocument;

    @Column(nullable = false)
    private OffsetDateTime acceptedAt = OffsetDateTime.now();

    private String ip;

    @Column(length = 512)
    private String userAgent;

    /** Copia del hash del documento aceptado. */
    @Column(nullable = false, length = 64)
    private String contentHash;
}
